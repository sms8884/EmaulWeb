package com.jaha.web.emaul.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by imac1 on 2016. 3. 7..
 */
public class VoteCrypto extends EmaulCrypto {
    final int VOTER_CRYPT_VERSION = 0X01;

    public class Item {
        public long voteId; // 투표 ID
        public long voteItemId; // 투표 결과(후보자 아이디)
        public String dong = ""; // 동
        public String ho = ""; // 호
        public String voteDevice = ""; // tablet or mobile
        public String voterName = ""; // 투표자 이름
        public String mandateNname = ""; // 위임자 이름 ( 대리 투표일때: 태블릿 버전)
        public String phone = "";

        /**
         * @deprecated 암호 제외.
         */
        @Deprecated
        public String deviceUUID; // 디바이스 UUID ( 기존 데이터: uid )

        public String signFileName; // 사인 결과 파일.
        public byte[] signData; // 검증용 복호화시 사인 이미지 파일 바이너리.

        public Item() {}

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("voteId:" + voteId).append("\n").append("voteItemId:" + voteItemId).append("\n").append("dong:" + dong).append("\n").append("ho:" + ho).append("\n")
                    .append("voteDevice:" + voteDevice).append("\n").append("voterName:" + voterName).append("\n").append("mandateNname:" + mandateNname);
            return sb.toString();
        }
    }

    public Item getItem() {
        return new Item();
    }


    /**
     * 투표정보 암호 (개표용) <br>
     * Data: Header+ RSA:inspEncKe(voteId+VoteItemId) + Sha246(voteId+VoteItemId)
     * 
     * @param voteItem 투표 정보
     * @param encyKey 암호용 키 (public key )
     * @return 투표 내용(개표용)
     */
    public String encrypt(VoteCrypto.Item voteItem, String encyKey) throws VoteCryptoException {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(64);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.put((byte) 0x01);
            buffer.putLong(voteItem.voteId);
            buffer.putLong(voteItem.voteItemId);
            buffer.put(getRandomByte(8));

            byte[] header = makeHeader(VOTER_CRYPT_VERSION);
            byte[] encData = encryptRSA(encyKey, buffer.array());
            byte[] hash = encryptSHA256(buffer.array());

            return byteArrayToHex(header) + ":" + byteArrayToHex(encData) + ":" + byteArrayToHex(hash);

        } catch (Exception e) {
            throw new VoteCryptoException("encrypt", e);
        }
    }


    /**
     * 개표용 복호화 함수.
     * 
     * @param src
     * @param decryptKey (Private Key )
     * @return
     * @throws VoteCryptoException
     */
    public VoteCrypto.Item decrypt(String src, String decryptKey) throws VoteCryptoException {
        VoteCrypto.Item item = new VoteCrypto.Item();
        try {
            String[] data = src.split(":");
            if (data.length != 3) {
                throw new VoteCryptoException("Data Error");
            }
            Header header = getHeader(hexToByteArray(data[0]));
            if (!checkHeader(header)) {
                throw new VoteCryptoException("Header Error");
            }

            byte[] decData = decryptRSA(decryptKey, hexToByteArray(data[1]));
            byte[] hash = encryptSHA256(decData);

            if (!byteArrayToHex(hash).equals(data[2])) {
                throw new VoteCryptoException("Hash Error");
            }

            ByteBuffer buffer = ByteBuffer.wrap(decData);
            byte startByte = buffer.get();
            item.voteId = buffer.getLong();
            item.voteItemId = buffer.getLong();
            return item;

        } catch (Exception e) {
            e.printStackTrace();
            throw new VoteCryptoException("decrypt", e);
        }
    }


    public byte[] readFile(String fileName) throws Exception {

        File signImage = new File(fileName);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(signImage);
            int readcount = (int) signImage.length();
            byte[] buffer = new byte[readcount];
            fis.read(buffer);
            return buffer;
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            if (fis != null)
                fis.close();
        }
    }

    /**
     * 투표정보을 암호화 함.(검증용) <br>
     * 
     * @param voteItem 투표 정보
     * @param inspEncryptKey 검증용 키 (public key)
     * @return 투표 내용(검증용)
     *
     */
    public void encryptInsp(VoteCrypto.Item voteItem, String inspEncryptKey, String outputFileName) throws VoteCryptoException {

        FileOutputStream os = null;
        try {

            byte[] aesKey = getRandomByte(16);
            byte[] aesIv = getRandomByte(16);

            byte[] signData = readFile(voteItem.signFileName);

            int signOrgize = signData.length;
            int signEncSize = (int) Math.ceil((double) signData.length / (double) 16) * 16;

            ByteBuffer buffer = ByteBuffer.allocate(240);
            buffer.order(ByteOrder.BIG_ENDIAN);
            buffer.putLong(voteItem.voteId);
            buffer.putLong(voteItem.voteItemId);
            buffer.put(getByte(voteItem.dong, 20));
            buffer.put(getByte(voteItem.ho, 20));
            buffer.put(getByte(voteItem.voteDevice, 20));
            buffer.put(getByte(voteItem.voterName, "utf-8", 30));
            buffer.put(getByte(voteItem.mandateNname, "utf-8", 30));
            buffer.put(getByte(voteItem.phone, "utf-8", 20));
            buffer.put(aesKey);
            buffer.put(aesIv);
            buffer.putInt(signOrgize);
            buffer.putInt(signEncSize);
            buffer.put(encryptSHA256(signData));

            byte[] header = makeHeader(VOTER_CRYPT_VERSION);
            byte[] encData = encryptRSA(inspEncryptKey, buffer.array());
            byte[] hash = encryptSHA256(buffer.array());

            System.out.println("DATA = " + hash.length);

            byte[] signTmp = new byte[signEncSize];
            System.arraycopy(signData, 0, signTmp, 0, signOrgize);

            byte[] encSign = encryptAES(signTmp, aesKey, aesIv);
            os = new FileOutputStream(new File(outputFileName));
            os.write(header);
            os.write(encData);
            os.write(hash);
            os.write(encSign);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw new VoteCryptoException("encryptInsp", e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 검증용 투표 데이터 복호화.
     * 
     * @param decryptInspKey
     * @param encryptedDataFileName
     * @return
     * @throws VoteCryptoException
     */
    public VoteCrypto.Item decryptInsp(String decryptInspKey, String encryptedDataFileName) throws VoteCryptoException {
        FileOutputStream os = null;
        Item item = new Item();
        try {
            byte[] rdata = readFile(encryptedDataFileName);
            if (rdata.length < 300) {
                throw new VoteCryptoException("Data error");
            }

            ByteBuffer buffer = ByteBuffer.wrap(rdata);
            buffer.order(ByteOrder.BIG_ENDIAN);

            Header header = getHeader(getByte(buffer, 16));
            if (!checkHeader(header)) {
                throw new VoteCryptoException("Header Error");
            }

            byte[] encData = getByte(buffer, 256); // RSA 256Byte
            byte[] data = decryptRSA(decryptInspKey, encData);
            byte[] dataHash = getByte(buffer, 32);

            byte[] checkDataHash = encryptSHA256(data);
            System.out.println("TEST = " + checkDataHash.length);
            if (!Arrays.equals(checkDataHash, dataHash)) {
                throw new VoteCryptoException("Data hash Error");
            }

            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            dataBuffer.order(ByteOrder.BIG_ENDIAN);

            item.voteId = dataBuffer.getLong();
            item.voteItemId = dataBuffer.getLong();
            item.dong = getString(dataBuffer, 20);
            item.ho = getString(dataBuffer, 20);
            item.voteDevice = getString(dataBuffer, 20);
            item.voterName = getString(dataBuffer, 30, "utf-8");
            item.mandateNname = getString(dataBuffer, 30, "utf-8");
            item.phone = getString(dataBuffer, 20, "utf-8");

            byte[] aesKey = getByte(dataBuffer, 16);
            byte[] aesIv = getByte(dataBuffer, 16);

            int signOrgSize = dataBuffer.getInt();
            int signEncSize = dataBuffer.getInt();
            byte[] signHash = getByte(dataBuffer, 32); // encryptSHA256

            byte[] signFile = decryptAES(getByte(buffer, signEncSize), aesKey, aesIv);

            item.signData = new byte[signOrgSize];
            System.arraycopy(signFile, 0, item.signData, 0, signOrgSize);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            throw new VoteCryptoException("encryptInsp", e);
        } finally {

        }
    }


    public void sampleVoteInspEncrypt() throws VoteCryptoException, IOException {
        // 암호화 키
        String encKey =
                "e4cd49b07981bf22fa99f8b89f8fa6f33d6953e44a1bdcb0b92b5ff26caa6d01a8ddb7da788054ecad43e99bcb686d5097f13505462d506f340e61e24ff4cbf135d016cf1ac25fc762f4c53a3062067c17e1bd1bce942007bb85671e80eb7ae591a4558c80302aa9457f0dcd5aabad207f147b6c013773d88a3ee097f606eca518b586f68d3058dd900dac8791929299afb4c8ef49da1e5be93c18b48623f37b70a8e61879b2506a960d824571f93dfba50ecddf66a1dd1f9f17c37073eaf3349c8ace6db08ac998fec44d2578ea2d3d628030372225af7412330fc27ebbf63769fbf9db11b097816326f4aa7e5ed771850d4f14034d41ee96a7b2eaf338d7ef:10001";

        // 개표승인후 개표키
        String decKey =
                "e4cd49b07981bf22fa99f8b89f8fa6f33d6953e44a1bdcb0b92b5ff26caa6d01a8ddb7da788054ecad43e99bcb686d5097f13505462d506f340e61e24ff4cbf135d016cf1ac25fc762f4c53a3062067c17e1bd1bce942007bb85671e80eb7ae591a4558c80302aa9457f0dcd5aabad207f147b6c013773d88a3ee097f606eca518b586f68d3058dd900dac8791929299afb4c8ef49da1e5be93c18b48623f37b70a8e61879b2506a960d824571f93dfba50ecddf66a1dd1f9f17c37073eaf3349c8ace6db08ac998fec44d2578ea2d3d628030372225af7412330fc27ebbf63769fbf9db11b097816326f4aa7e5ed771850d4f14034d41ee96a7b2eaf338d7ef:54abf7f23bf51d03d7d3b52cb5b7d6a3bb7d48137aad76ed5b8946b793293aafdb8b39a7bae25b0b3f4f9b9b111a7321d928e5632f4643b6f37fc30f9a61503bd29b506592548e575e3290ee68cb4df50c6d78ab5ab68c67f0287d31d7a9cd5039a6a4161bb637836ef986e51101e94ed65881184616fc630d3e19a582e1ab1ef6a179e8f87a46d25fea3c91326a39b83f78c54a284e49fc673e55de5841fac8460eada480a4993e7d5015249eff6bf73372e59b3a61014ba1e2270a2be4260ae7badb45922608c3f97e2d63ab80a24c0ccf94347ff43fd3ba05b2d3bd46170cb0ee73fea96b48db7a80ddf41a1686fc899cc0178feb3cc6fff9128985a32891";


        VoteCrypto crypto = new VoteCrypto();
        Item item = new Item();
        item.voteId = 10;
        item.voteItemId = 101;
        item.dong = "102";
        item.ho = "102";
        item.mandateNname = "ABCDEF";
        item.voterName = "홍길동";
        item.signFileName = "C:\\dev\\test_image.jpg";
        item.phone = "01099999999";

        // 투표후 암호
        crypto.encryptInsp(item, encKey, "C:\\dev\\test.emaul");

        // 검증 키 복호화
        Item orgItem = crypto.decryptInsp(decKey, "C:\\dev\\test.emaul");

        System.out.println("VoteId    :" + orgItem.voteId);
        System.out.println("voteItemId:" + orgItem.voteItemId);
        System.out.println("phone:" + orgItem.phone);
        System.out.println("dong:" + orgItem.dong);
        System.out.println("ho:" + orgItem.ho);
        System.out.println("mandateNname:" + orgItem.mandateNname);
        System.out.println("voterName:" + orgItem.voterName);

        if (item.voteId == orgItem.voteId && item.voteItemId == orgItem.voteItemId) {

            FileOutputStream os = new FileOutputStream(new File("C:\\dev\\images.jpg"));
            os.write(orgItem.signData);
            os.flush();

            System.out.println("Successed");
        } else {
            System.out.println("Failed");
        }



    }

    public void sampleVoteEncrypt() throws Exception {
        // 암호화 키
        // String encKey =
        // "e4cd49b07981bf22fa99f8b89f8fa6f33d6953e44a1bdcb0b92b5ff26caa6d01a8ddb7da788054ecad43e99bcb686d5097f13505462d506f340e61e24ff4cbf135d016cf1ac25fc762f4c53a3062067c17e1bd1bce942007bb85671e80eb7ae591a4558c80302aa9457f0dcd5aabad207f147b6c013773d88a3ee097f606eca518b586f68d3058dd900dac8791929299afb4c8ef49da1e5be93c18b48623f37b70a8e61879b2506a960d824571f93dfba50ecddf66a1dd1f9f17c37073eaf3349c8ace6db08ac998fec44d2578ea2d3d628030372225af7412330fc27ebbf63769fbf9db11b097816326f4aa7e5ed771850d4f14034d41ee96a7b2eaf338d7ef:10001";
        String encKey =
                "a715311b3735b35690b0a003727c30f4e615a4f306ea3b1e4b699b0c12b2fdf05e21ceaf58f849cd32e0e7b8c48b4bacc1d2eb3a7da2323320f3cf71739a0c250310c13ada6df187d18074deda1df8ff0f712f846e0c87e43d99a898af8ef6e680c70d6ce62f94126a64d56a9c70d9653ac9dd1c0016ad15d1c95d7767826275304ea49b135ae3194278684e9ca1eafd7f30ea251cd77b1ba674c11d3e1b02f621d47b419e8f43099addb4a09b2fd4ebaf54880a4a5c8b8482a36c4764549edaf4d9d95d21cb74d82ae5547f0e0ab414a0ba1714e113cf8849ebdf0c7ad43e54e9247512e26e654f737560676af98365548b6c1ac3719d512bcf4e2ef6ed913f:10001";

        // 개표승인후 개표키
        // String decKey =
        // "e4cd49b07981bf22fa99f8b89f8fa6f33d6953e44a1bdcb0b92b5ff26caa6d01a8ddb7da788054ecad43e99bcb686d5097f13505462d506f340e61e24ff4cbf135d016cf1ac25fc762f4c53a3062067c17e1bd1bce942007bb85671e80eb7ae591a4558c80302aa9457f0dcd5aabad207f147b6c013773d88a3ee097f606eca518b586f68d3058dd900dac8791929299afb4c8ef49da1e5be93c18b48623f37b70a8e61879b2506a960d824571f93dfba50ecddf66a1dd1f9f17c37073eaf3349c8ace6db08ac998fec44d2578ea2d3d628030372225af7412330fc27ebbf63769fbf9db11b097816326f4aa7e5ed771850d4f14034d41ee96a7b2eaf338d7ef:54abf7f23bf51d03d7d3b52cb5b7d6a3bb7d48137aad76ed5b8946b793293aafdb8b39a7bae25b0b3f4f9b9b111a7321d928e5632f4643b6f37fc30f9a61503bd29b506592548e575e3290ee68cb4df50c6d78ab5ab68c67f0287d31d7a9cd5039a6a4161bb637836ef986e51101e94ed65881184616fc630d3e19a582e1ab1ef6a179e8f87a46d25fea3c91326a39b83f78c54a284e49fc673e55de5841fac8460eada480a4993e7d5015249eff6bf73372e59b3a61014ba1e2270a2be4260ae7badb45922608c3f97e2d63ab80a24c0ccf94347ff43fd3ba05b2d3bd46170cb0ee73fea96b48db7a80ddf41a1686fc899cc0178feb3cc6fff9128985a32891";
        String decKey =
                "a715311b3735b35690b0a003727c30f4e615a4f306ea3b1e4b699b0c12b2fdf05e21ceaf58f849cd32e0e7b8c48b4bacc1d2eb3a7da2323320f3cf71739a0c250310c13ada6df187d18074deda1df8ff0f712f846e0c87e43d99a898af8ef6e680c70d6ce62f94126a64d56a9c70d9653ac9dd1c0016ad15d1c95d7767826275304ea49b135ae3194278684e9ca1eafd7f30ea251cd77b1ba674c11d3e1b02f621d47b419e8f43099addb4a09b2fd4ebaf54880a4a5c8b8482a36c4764549edaf4d9d95d21cb74d82ae5547f0e0ab414a0ba1714e113cf8849ebdf0c7ad43e54e9247512e26e654f737560676af98365548b6c1ac3719d512bcf4e2ef6ed913f:8980692f4a9a49029e849ccaf976b71b7416afeec8ec0ec6f9ace94a604129b69ce119a53884ecc7906d82858e4a4c76d9f3e85df4727cf992ced3133cddb3cce9deea06b327f3b3a07824b619aebcf1bb159cd35175c287d522159bc22ed7860ec8c185b351c87bf74f104b727f7489587e34170d039c61ee89d6ffa311ac500c8810a4fc7dca8ae683594bf2db81717f47ba6fc233ba2ee4aee187830daa10130d712bec3271122cfdde3f81e9c7023d835e0a384c5dea57665dccaffea13581a571a27990508fc719eb16071c442ea6c0075e5303b59d9d754c98b734c1b27a3839ae23d72fbca3709f46124998debe3ce9e203412262c248cb8ad2ed8f01";

        VoteCrypto crypto = new VoteCrypto();
        Item item = new Item();
        item.voteId = 10;
        item.voteItemId = 101;

        // 투표후 암호
        String encItem = crypto.encrypt(item, encKey);

        encItem =
                "a101454d41554c000000000000000000:495e90d0541e1e243817e0570ed34a4999a4f6a6428de80b341cc4638737e4cc409edef13682bb84c79e94f7f261b7c28b55a88156115b1011bf94a304619d387d4e1c43b6ffa6564a0f806ec6afbb7902608f5f3c91245fd12198bf2555a824ddce4e1c08999e1674d9c80eacf982d32c26adbdc49bdbd2c7af8df614169bd4cac596bcc34c9016c9d65ef27ee57b5c922905c9102a2f132be2746bff110b1ab65d92a9cc9f93c3792d738314aceae7a77556d8a1f17db7ec998c90cd3dd8cc8538692daba7d61cb4f47f9d5769825376acda7eef4aa39bc8fa756d055b8dd7d31aff453c557a1da2aa47b9f873affb435bed2d3364cf9d5d8c39516f78317f:9a2ddfe4959962daeabfe0b378b78335c6d7aec25654ef885794dbdba48c983d";
        decKey = "f4dbe26d8d751dcd27cf6ff013b6d7c7839c318327c0740cdeab4de17f937ad444ddc758eb2960d949484a61feedc1b6088fbc7effe594b6def6444307c13bb0c191c851e6ddb2d13fae9c86204b7b8583f5d78a69a1024164dfcdcad93c09a0230ecbf1ee6ae31827c8b5a03f9440eba53277a70efe147a8c2b2aa16075c6bb43855497f9ccc35696537722db29d3247eb884b1ca48b62f22ea61717755c142346e066fb31e9765d7b485066047198a33ac9cb3fb018d86e73fab3bb2202858e5baafecacc59a9de6f32984428657e14c41a9ba198204055a63688143c2c5b2b1b6629b6df2381419e8d1ccee72165d9decbb0e4094c5f7b96d3d30e001bf5f:27493c4c1d7d23f9eda03fb282473cd9bb090d7ca09d2541875c6ed37657a0467c8224e0e4623032ccaa379cff7d002e87b3202834408e9ac4ad388894aef137cbe6eb00110d930a35b07b569dc9de98ae727c51addef270b191a3001fd89d26bf593969a8ba2793919687659dadd8b8b639a1af011dcd055885f88209eafba671b5e7d5ce8f73904ae16d05c7008a5cbfdf9f99d1f5de995b70737e0495b2ede6bc773b8f51f13550ab3d30389a7992a4d6069e2a58f16a7db29d21ce86f0a45167d0d4172b8c44f52f5cc2bcc8025636bc72b2cd0956de12718b90d743eee09896eb8df9e406550a1d3f8a3582f6629d89f6af08714d294d0acbab29d40389";

        System.out.println("EncItem:" + encItem);
        // 개표 승인후 개표 승인키로 복호화.
        Item orgItem = crypto.decrypt(encItem, decKey);

        System.out.println("VoteId    :" + orgItem.voteId);
        System.out.println("voteItemId:" + orgItem.voteItemId);

        if (item.voteId == orgItem.voteId && item.voteItemId == orgItem.voteItemId) {
            System.out.println("Successed");
        } else {
            System.out.println("Failed");
        }
    }


    // public static void main(String[] args) throws Exception {
    // new VoteCrypto().sampleVoteInspEncrypt();

    // VoteCrypto voteCrypto = new VoteCrypto();
    // voteCrypto.sampleVoteInspEncrypt();
    // voteCrypto.sampleVoteEncrypt();
    // //voteCrypto.sampleVoteInspEncrypt();
    // String descKey =
    // "a101454d41554c000000000000000000:55bddcd46c6935f83ddd58292868729d7e7de3ce26428a5379d5c210e3c31fbccb020550993eb2da71f8548b9a2b11bb8f19ab1cb5254c45dba518f71fb4a2e8f73cc6200e343ba7fb256274f06d830e970141835d52e22e57a1f1720e1b95a164d0d3af1eebd978d1b92f72c73c38474c258cc1de2f09378d792985b8373b5df206744862dd4a8be2ca372a4f9a594dab3162eb1c1995921c439aa27c7326aabed5e6d8bb3096fc3cd38ca6a5698a02976f0d9f8a800832d8bc8e4aae44ff2dbb0eeb3a99f016055f67ffa3bc6a801d882386a24ff1d4ec1df2806917cf1051a81bdcb890ddd74553926b125e5214201e58303592595de918d856b7cd3054fb7145e9dd2f8e26f108c716277354a921875eaec6ae41bf492726d42e0f385e10c7b9fdf13bfb14619f4d445e07e833cfe0c955abf3e8cde5679c70421ca6b8683f70a6d2ebb7b933748b80f1e248932dfaf829924beccb1b80bd42ecba428d7455b820608e10a2f009a1c6f75eb2ad48121006afb6647169a99b3220458a0013c9413df2370501692450bc5e11a1b65f1f5a2fafa861359d2c4a70e0a5e32d92c8c76599a1ef0a906b2fd7bf45f180f74069260468c6dfd3aff310802cf9863966478e50c6f275c7c87a8cabc91c902b3335d78a3e90880a8e8009e2f50a0e807911f6f6cfcda7d4a5842fbb382555261eeda0a237c023818cc6539116238d0ed99911d79158b6f517d7cb7603ff69a9b3dcd04954e7ec901bd1423e021be63ea54c12c08c04371f8345b8b3c23214a431aaba6b4afbc1590da34e49d607d150ff76d4989fe2417c71ca08c3d673adb8cdce0e10c716d335cc127fd79978926391d6da7aaf5460687cc440fe5b2096c121a1a2e6360c2b5da083016e4f232e25350aacae7b406ce3485b9d608e8a15f94cd24a35feee6d5d1c29c7bc04d58ca51f4f27e3d8569e19aa8793ab700d187bf43ebbe3c2811ac68d732ce8c69a959174baf475cfee4d76e9bb942fb9f3e71936fc84b97448284373bb28adc590f4cfa10e1511a303304139fd4632ec9ecec3e0c43eefde3736f0d1fed7f0b086f4b85daadbf00a063b271922cf76cf0e2fa6ff2b0898da06121cde4ca5e3e93600e8967f5efd435ac0a0c2f1d5b7dc80ae1f052a4ce8a28d3bfb24f655565f0b9538528e33a23b0a444507d7e917bf00bc29963d92be340262e902184712fb296bce60c93728e18c8ade68c0c50ed482190e7da400e741163b3535b842aa87271abb701c03b08241f9398174b3a8a4c2c2db0c67a8125b37632f97bd3762ea9687bb61804943847ebbd7f9e9cdae25d9f3c6aa3ff79cf99c1568f2896995f5308313ba7e26f4e00e1cb32a567aa0534f515494bf3a974fc0b61ebbb9b3caf9e01c18978c8d92337f6c55ba505fc260550648bec22bfe2c7b8854e5194f369b6b5718:a553a13f23d4cb5e63bf92acfe3c2ebcaa877a2e59b6b923250bbf4dce7980b8";
    //
    // String mPwd1 = "1234qwer@@@@";
    // String mPwd2 = "1234@@@@qwer";
    // String mPwd3 = "1234####qwer";
    //
    // EmaulCrypto crypto = new EmaulCrypto();
    //
    // String decKey = crypto.decryptVoteKey(
    // EmaulCrypto.KEY_VOTE_ENC, mPwd1, mPwd2, mPwd3,descKey);
    //
    // String descKey2=
    // "ec5dfacea1c32f500fbf655c8a4c40cead72bde5d6139ce4be68b0984a49f3f8513d63dc8934bafb887572fb2d5400762ee97acb95eb24d33f1ccdbdd48422ad303c12951d552b51e02ebc22115724f89261da444d9bb741f9cbb3d8999d94dba906a6f8a057853d5e01b41221bd15f3b9db580abfe272ec24792ba256189d19955fbebaa11d547f7aebda5d486ae5f9e856c070064b0a25cd81dc6aa7e9ecd51e7943b4452eeb5e7495076e6eb495835d39718ea91a78f473b2f23c6907ea77f3c62610496cd48f7497aa31a42c6aa7a6783ad763be6771d183d233ad403775d4954be59baf1abbbf307a6e731d2fe22200dad0548ca619e8a851a140a1cfb5";
    // String descKey3=
    // "ec5dfacea1c32f500fbf655c8a4c40cead72bde5d6139ce4be68b0984a49f3f8513d63dc8934bafb887572fb2d5400762ee97acb95eb24d33f1ccdbdd48422ad303c12951d552b51e02ebc22115724f89261da444d9bb741f9cbb3d8999d94dba906a6f8a057853d5e01b41221bd15f3b9db580abfe272ec24792ba256189d19955fbebaa11d547f7aebda5d486ae5f9e856c070064b0a25cd81dc6aa7e9ecd51e7943b4452eeb5e7495076e6eb495835d39718ea91a78f473b2f23c6907ea77f3c62610496cd48f7497aa31a42c6aa7a6783ad763be6771d183d233ad403775d4954be59baf1abbbf307a6e731d2fe22200dad0548ca619e8a851a140a1cfb5:8694c01054f47084f26274d12b015dc90fe30c12c840648dad88a83c1dd85bac207d86d573dded34f8ae7ee85b17c0278a706ff56907f8f0bab7a8c1943624145e29661f37135e5601023b265e9f32b2c18e2b9bc6608a086bb53b5271acd2abfb62894178f23c3003cd8e7ce06eec07d8e3f60e30d13bc40ec45b3ed42cb18d5e688532ff5eaf5fe3127c0fb657bb8d1a0f917f09ea8dc21df1d91172b7123acdb83281877ae5bf9511d2b481212aa4c1e1b5a286eef02d48fef31dfc721ee6cc2932c4ee0538ce4214e216a34e323ca26ff58c2dea704d149b6ff36c231529b038e5cede661cb4030c6fd041cae4656e5a7f2232cb01bb2a74a037ca11b481";
    // System.out.println("TEST : " + descKey2);
    // System.out.println("TEST : " + descKey3);
    // System.out.println("TEST : " + decKey);
    //
    // System.out.println("TEST : " + decKey.length());
    // System.out.println("111" + descKey2.equals(decKey));
    //
    // String
    // encItem="a101454d41554c000000000000000000:0f8a4af1422bcfb59d44af8fa2c698fc3d0ec95f5fd8c5e035218c6ae48de250abc7d000d27908b025726a438044ae1b65e35076a8780c574b223d9de4728ac9be3ea6c3aff00593929fa39cff04724182c1aed41103dee406e83a2f2edb31efbe8717d70071e9ff32eb9c29e735f9905da5db8591677214540d79e47a07a30fbb417d449a1f035311d5edb9977cd7f43f25ca9618d91a188c9f82e8befee8ac394d235c83966550c79bf10932b3a404b2522a6ecf72be61d92c4a5fe284b543167e42cee31d43b3fb0c1e4ccf293a3d3c51369d7499730f54b93671fe349c810b5095d7fe76c8c83895203a2b6388e1aab09a6294a55cf0456b8e7267d8f662:0725be2be3816640f6fbfe614dbbdd9ac8e5505a6f1562f32cec575d77ad2ea4";
    // //String decKey =
    // "a715311b3735b35690b0a003727c30f4e615a4f306ea3b1e4b699b0c12b2fdf05e21ceaf58f849cd32e0e7b8c48b4bacc1d2eb3a7da2323320f3cf71739a0c250310c13ada6df187d18074deda1df8ff0f712f846e0c87e43d99a898af8ef6e680c70d6ce62f94126a64d56a9c70d9653ac9dd1c0016ad15d1c95d7767826275304ea49b135ae3194278684e9ca1eafd7f30ea251cd77b1ba674c11d3e1b02f621d47b419e8f43099addb4a09b2fd4ebaf54880a4a5c8b8482a36c4764549edaf4d9d95d21cb74d82ae5547f0e0ab414a0ba1714e113cf8849ebdf0c7ad43e54e9247512e26e654f737560676af98365548b6c1ac3719d512bcf4e2ef6ed913f:8980692f4a9a49029e849ccaf976b71b7416afeec8ec0ec6f9ace94a604129b69ce119a53884ecc7906d82858e4a4c76d9f3e85df4727cf992ced3133cddb3cce9deea06b327f3b3a07824b619aebcf1bb159cd35175c287d522159bc22ed7860ec8c185b351c87bf74f104b727f7489587e34170d039c61ee89d6ffa311ac500c8810a4fc7dca8ae683594bf2db81717f47ba6fc233ba2ee4aee187830daa10130d712bec3271122cfdde3f81e9c7023d835e0a384c5dea57665dccaffea13581a571a27990508fc719eb16071c442ea6c0075e5303b59d9d754c98b734c1b27a3839ae23d72fbca3709f46124998debe3ce9e203412262c248cb8ad2ed8f01";
    //
    // Item item = voteCrypto.decrypt(encItem, decKey);
    //
    // System.out.println("voteId:"+item.voteId);
    // System.out.println("voteItemId:"+item.voteItemId);
    // System.out.println("");
    //
    // encItem="a101454d41554c000000000000000000:014ccfcf5f101b53e8a704ee56cdfa7e46d5e2c66362efea75147bc9fbc93b1b2d7fe43b4a49cb0ba62fd9638393e731a7c602cb360dbb08a753af5ef450b8866af5c7c6fe106fad6f2023944a28ef6a19986d253d4dc4ad0d363856a240858f3f0e55c48bcb3e73fe5a5cdc6802c64cf7649408868009c19e4d0c4356f7c5153fd12f619ab9401944659fd2dd033406607c8747656ead7817174270bd5f16e2ddc76e388fd7100ede1567d8002c0b4fe325c9f2376ba4c19167423433e12f3a0830bb739eb9dc8e491fc37786b24f8ae263df18d12aaa7d62ea132b31e7f4618321c333c92aa7347237d4b026c31bd6ee26aa52b6821bb0573b5e578b2a83c4:d64ba19bf8f7d0f9a5cc8832e2f2d4e4c1286c106f7be58267e8ed3de1b03b63";
    // //String decKey =
    // "a715311b3735b35690b0a003727c30f4e615a4f306ea3b1e4b699b0c12b2fdf05e21ceaf58f849cd32e0e7b8c48b4bacc1d2eb3a7da2323320f3cf71739a0c250310c13ada6df187d18074deda1df8ff0f712f846e0c87e43d99a898af8ef6e680c70d6ce62f94126a64d56a9c70d9653ac9dd1c0016ad15d1c95d7767826275304ea49b135ae3194278684e9ca1eafd7f30ea251cd77b1ba674c11d3e1b02f621d47b419e8f43099addb4a09b2fd4ebaf54880a4a5c8b8482a36c4764549edaf4d9d95d21cb74d82ae5547f0e0ab414a0ba1714e113cf8849ebdf0c7ad43e54e9247512e26e654f737560676af98365548b6c1ac3719d512bcf4e2ef6ed913f:8980692f4a9a49029e849ccaf976b71b7416afeec8ec0ec6f9ace94a604129b69ce119a53884ecc7906d82858e4a4c76d9f3e85df4727cf992ced3133cddb3cce9deea06b327f3b3a07824b619aebcf1bb159cd35175c287d522159bc22ed7860ec8c185b351c87bf74f104b727f7489587e34170d039c61ee89d6ffa311ac500c8810a4fc7dca8ae683594bf2db81717f47ba6fc233ba2ee4aee187830daa10130d712bec3271122cfdde3f81e9c7023d835e0a384c5dea57665dccaffea13581a571a27990508fc719eb16071c442ea6c0075e5303b59d9d754c98b734c1b27a3839ae23d72fbca3709f46124998debe3ce9e203412262c248cb8ad2ed8f01";
    //
    // item = voteCrypto.decrypt(encItem, decKey);
    //
    // System.out.println("voteId:"+item.voteId);
    // System.out.println("voteItemId:"+item.voteItemId);
    // System.out.println("");
    //
    // encItem="a101454d41554c000000000000000000:082b779ad50c53803656094a252fa7abb0e0203811241ca4f13909cc45cc7749c6fdd70dc5e537bd760354ff6619a81bbfc14cba591a277d7c1bf797836c7d7ac2f53af9b34d0c0b37b3c5649f2c515255a604d4da0c76da893a00e63a55c86dc1f5610038498116b29160645c19af2a886e935861449894cf55df4ba5a5d83bc47bed8a589d3d35f072326e230c5d042debc6d49b573c19d673dc831a48ccef9f68a70ce06a564edddd831fabd5406322087ec27b62add82c325a7cadc33ca6c5535a905ca42d85a9f263c2420d22ce721640e8165f050afaecf80c3e361398728b7556682a96073b57e6452a637fb379f4a986fde9014d94c4b64296a05a0e:2301e8a7b817af5176394bb3ce0a287f19a9c9c890bad397fc2bac86860bb899";
    // //String decKey =
    // "a715311b3735b35690b0a003727c30f4e615a4f306ea3b1e4b699b0c12b2fdf05e21ceaf58f849cd32e0e7b8c48b4bacc1d2eb3a7da2323320f3cf71739a0c250310c13ada6df187d18074deda1df8ff0f712f846e0c87e43d99a898af8ef6e680c70d6ce62f94126a64d56a9c70d9653ac9dd1c0016ad15d1c95d7767826275304ea49b135ae3194278684e9ca1eafd7f30ea251cd77b1ba674c11d3e1b02f621d47b419e8f43099addb4a09b2fd4ebaf54880a4a5c8b8482a36c4764549edaf4d9d95d21cb74d82ae5547f0e0ab414a0ba1714e113cf8849ebdf0c7ad43e54e9247512e26e654f737560676af98365548b6c1ac3719d512bcf4e2ef6ed913f:8980692f4a9a49029e849ccaf976b71b7416afeec8ec0ec6f9ace94a604129b69ce119a53884ecc7906d82858e4a4c76d9f3e85df4727cf992ced3133cddb3cce9deea06b327f3b3a07824b619aebcf1bb159cd35175c287d522159bc22ed7860ec8c185b351c87bf74f104b727f7489587e34170d039c61ee89d6ffa311ac500c8810a4fc7dca8ae683594bf2db81717f47ba6fc233ba2ee4aee187830daa10130d712bec3271122cfdde3f81e9c7023d835e0a384c5dea57665dccaffea13581a571a27990508fc719eb16071c442ea6c0075e5303b59d9d754c98b734c1b27a3839ae23d72fbca3709f46124998debe3ce9e203412262c248cb8ad2ed8f01";
    //
    // item = voteCrypto.decrypt(encItem, decKey);
    //
    // System.out.println("voteId:"+item.voteId);
    // System.out.println("voteItemId:"+item.voteItemId);
    // System.out.println("");
    //
    // EmaulCrypto emaulCrypto = new EmaulCrypto();
    // String encKey =
    // "a101454d41554c000000000000000000:3eb8ebe8c9b0a2e5e58316d3311ccda7fdff78d3498e3b2bdd9154acc4e56a6d8d411084fa78104695e2192cc3262720acfe901a064335f03eca49eed3a6391931cd6a5ba5d63cf86f7fe54edfc03bfcc29ac1b065ffbfdf0665546569f3ad93044bafbf044cc722bf862729065fd3d59404efc054d8d7909f27135f87ac9c30bb96e038a44210c184987cda0be47e97b69eab468040cfc552c2c28e0d855c1dfd16dacfc365d1b6170f963de635a10c85fcd4c0f6eb50a86d1da77b2ab1e0ed674b2bc594f4cfc47b7365ff98648e648ab14d513cf913fc383440e02c9c0877582e7e238bfa2bf34f09d0cfe768114434883d89083c065195b790cf748fe4bf89b4ba4510739058a0fd624b52cbfa45c9ea0939f842449af9099c91afc093b09f2ce06bdd88af41b58efc0e5d0e0cb8602e7cf970b05f392d4beb6bf9e5c2eb86819c2f71419f1816e376b725dc9650c5bc595a3d132e2b90a8177198f5b4a9c0324d8140834a37d6d8e24877f7455585cf6592aaacae7b84601ed04de44378cc94f5712832010a3a7545061effba976b477621749152e70ea7619103b57adf3b268caa5f14b094aeb2c72eb550cae9f3bfc7996156bbf4e7777a888253ef5e2c4058dbf3850e81f22c3580fc65f8db3b6d5dfb65c2a21989a7dbba57d33df321f1dfef157c330ac0237f901c0bb740d95ce71e3abd0b45f7892e939f90fb3a5e1b7b7e3bf631a5a23dc8961ec69382b007d2ca5e8d6ec649ce2c874a7bbc84c2c26a99c8c9e8c6efc2a9506719bff608ebc3f4f9c321d6ba1a12b161bd2d2c221326ae1d221c24a458830dbb8bd262cb681359b5c07fbdd1e590226e775d49ad9ef741e23603187b72450bb92231be5e3ffc0be6948fb7ca39f3857b64f1849fd16687b9098c1638a8c8444e8ffdda1b0561d1f1a2e2c25dcbfd4c056b5813497b15e3f431244f5b4fda569901f801d82e0f1dfd55e20bbae04bbde4bd6388b1b9a3568985508fd3f962ef94a197b7ecb1ef4c6064f43a11966ec6470739c69aefba58cbe75fd6a9da0a7eb7ba8fe3744216ba1f2386c4c7ce488b63bde9d2332e85fe735b504ac7f726cf2a31748e569c5255914cb9418c5aa09eca13727732c8fb0b2fd18c62e50cc5e2998391657ddabe507c86f15c8705f5961da8fd256087a2542fdc4b3855ea4f7cc6791f8b11f272921bd3dfd5bc2eaac7cf6f41fe4b2ed40133aeb4ceb14362c17b12f4ec0638f984be3fefae89af7eb3fd520104a8797af429351318439a9e8357848b0093ea3646ac3f927b9fd4a09c89835a834a47b80788744454e1f5d1b29ad4d680ce8eedc74c9a0cc2a0b45024c886490129c0b8943bea99a9ac967c914f3b9f30bd6a067d8753fcee1fa6509c84e0e89db4949bbec1b848a77f8672b587b7be21e4cb1f22f115bf76c006fd7641efd057:6c4c2b94c5a8a7081b019122ed6d20119c94922c0995007088ef897fea817ec6";

    // String encKey =
    // "a101454d41554c000000000000000000:316ba3161beb042db3d565ed0ffc2673b2ff810b5e6151901ab202ea74641d96f261769a2ecfefefb4ff3bf06370cbd29d8fd67ad3313366c5d479e7c2b9fa52244e3da9006c18907151afed7a24093f1feb6be540218a89be38ae532ef4b9b0f09440e8a29b79c0714b88c7cbe8702fd454ed84f9108f857f6c710273098f15ff837a042a014c6b6e9f4d00cb35e704b8d8140e968490a500b16cff225a67cee09fddcab6bb966ba86bb6a937bde9ccb472ffa62cc566c59c1aba541464a6017af43b70f85cd3b0df1d194b55f833fb7cf75e2d6f56f0809513f69996e30502e571601d92675ee049d46e54598f2f5935bf9bf2a1452c4a3039c90cbbc6ceb226a81a32bc7f55944117edcdbf4150c8d09cb90b102edf3534f635e9491cb4e718de3e94da73a18379ac10ebe46ba01cf76cff3dfe718455fc97a51e48a72cd7bee6f973c77230f2e5b814f9002bbc4a650aa45563932eabc751dcb891657ad48fa43385ef2ce111da1363fb9302ce4e733defcb90b9826c073f4ad7ee582187fd2c9342bbf0c2f9d9758f0ae9d7d5159f0fb092844645d377094ae30fdb86e2cd1dd4735e976a8725371f4ab28f92e2cbe232b04e7257bfde89af6058a6558c706e1490e8ce67a3b6270b7ea7919e40afedb4f0a06ab397988f346698c108d5d31c5bebf3482da7a2d04e2852ae317e7abd3e6852b6a1ec02bf57ce671624fd6c076c24af5e491aaf419893120121f83e08a7c3fb242034b9ed3a79a92410b5d69829219da72cb58501dc3b3445b447e374bb5d8ce218250df35f3a00743b54b01cacd2e684d33058a38fd874709428eb4d48e1a1fae437aa1fbadf04957dee6fc1b85220de2af2488ca3917471b8e9a5bd96e2d2b1a81fb56a3d3d7a28e1476861de74afdc15cf3cbe2e50c3c9dae7d944533c09262770b158f57dab4ede33baf0aa504b9a6d10b2ebbad42cdb73257d091557beaf50d942f7ea0559635f66aa255150167f9494858a561ded386263d9483b38fa24da9a82deb7132f2b093303712e9817fdd6de3b3511618c1b1165b4441ef63b88f7b0fb0bed637e53e2e526277daaae3be99b21b7f4af6b91dcc97422b2401ddb64005db2a57d1483c1548802e91fe9efa82d91c92a0270f9ae53f1349b1cc9c3e42cc5d142baa41a4c14306be58065a2bf5bb49772132a53c90479ab37eacd6222b53ae822af7c51dea1910c635a88cb7509702517731401ea041b0fe58d3b818ff71970e995afe51e2b8655a600ba0b2d1d36cd87d01a364ba948711698788f14aca5f99bb9c2178cd3adb9954dd7d558f683b401fb6192f6c9ab948d42524eb6f5c463c4fdfca5dd4faee0cc3f1141fb627454a7fafbfd81b3a08546a202fc8f3bf87f1b75fb4a4f9384b50e20468b9078decf2a68d3809adf38c1ff50d049d3a8c48e9f506b965444:055c5f3b239edb31df71d6b2cb7fa32da23c0caa12eb7f6a0d193f62353cc84f";
    // String encKey =
    // "a101454d41554c000000000000000000:ca8b4edaa259ba6b31288de79c36903f60a7677b91cc72bc5d5ccdf17ada1ecd35f1fc98e1996a39fd2b6e5f5f032032a0fda695701cdbe5daa826959275f975796b93fc4395dff192b9306cd09a7eb1a7fab12ca150d500e6f01e1572aa4f8030389df5ef0400cd377c7fdac0e2a138f223ee097a433f18ef2399704907528645a5367820a88f54e3c89be0a043b5bdfab83002d8ceccc1503d6b7cfe98bf36841111992926d6e7f24a28017c2c67314f1a1a28dfc46058221dc286497352f766ff2efde72bf3929d52a6f9060181dd91683705f431261a1d00ceb562a8af0c44864c0b69965c8fa48933d1148a4075e7b25ce5fbde15d0d1e8fcd75a8fd3f5a61a6fbde91f846d7bcb3088bf2a0b67daebb742c15454280dd1fa8df9a373239ea6713cebff7e7f0a6da659b771063419e1360af6ba9052945da9472e4cc233e71f0c878e6ef12d46e030bb9a842058af873d4f2fa506bf61f089e8e912e56821393c5df9766934aab9ad2415c08517dda8deda570cab3cff26387f27630d463f3fc0410bf3657024e293cf17f226ac5ffe65e9b59fda8e079882797d4394e7b0da218974df5bf025c2f83f3b8a9ef11f5038b82649f96144b27e18068d8d5cbbc4d0ae4ffd42823ef3a938347a17eed3a9c9d894588d7585ea7e8e90b865816053160cdf8c00d9e820b7fae41ff47a058e863e4337b4e805c5ae5fd45b79287601f2bd1096c6ff805d945b2afae6658dfc408da17733f56550c22428572f68284e99462138ca8e43a1578a1e6474a89f7b02556f36b6459d5baaed7ae5f7efcd26486bbccaf5b037a919ac3c317bf0ed48e348669e57771986b70b10aa6976c8941c1abdc2edaf3d3435cdefc0e0aae7802c399bb6cc3133002065967d20284dd13e61293d86cac97b73a000a077bc5497c1ac03aaa6c07b587f8a01b8f4c56f8b459f3aaf511765ba53a5a1023709055cc42c5866c4f9a474e3b79b940d5a0265f4d6234c57a70c997dc11bb682cb326ce0979b5aad61dc8692d4e2e557ad49843675c1fc4be8dd1a9542e5382bf6e755c5b5002b48002ad78ec067e69f881fbf38247fefce4b9d2e5c129f3bebf38f94af0383d4afe9333c97ce936225a8848c8bbd69751668496f62dccc326ebfb770be9617d1e98d4625fa60d6185c037d1c7a6b785a58b263050737fe77de66bfb301d29a046ee064ad98c626edb1e9e202b78d03e99551b4358a74794bad0149753e80aa8f21469f96e7a53c6aea98f9d8d02894dd46cb0c362578cb43c96043b9c32b0f87894e0462b6d929659fe53400b5f55faa9f482b529c227a8921ed53d1c0335ccb47ebac00b3ce753356ae6a783baeba51d41efbe21b7e89f6a563cd57ceb53b3161839010dff8f1691d8ee8e7df15ab613f34c1892f1849f5e9037af7737978b9669618dc80b28cc1bc95:9807c9b5bee134940c2006a73b99d45f2a8e71db9699631ae631bf0ecafe0054";
    // String encKey =
    // "d222bade138cfc0fde79596d6fe3b4ec43d6043c2066b496119b7fbf26e5a7c1bc98746c1810533d8b46cc89be3678c88ca0a597042c95c997ab98473cb8976070af3508f6fbe62cbaea62d022cd1c9408653dfb434578eb613c81cee1bbec83eb7c4e4a14fb760556659e572d13e0ebf7f2eecbe546b473162c047652d0811b588127e07968ac6a3017498e0c4ebda0318676d038a1a03b46355c44cc9e3d29ce686b09b296d3ecac74c9eb3ed00a15582c7d047aa1b48ab9dc774fb929c1c692fa7efe21153558c059c06d16aa8f4f145120c82f9171040f2fbad7d859f3250af7dc11714f88911ad451a289e669e2c734d67667982aface878d83b5769455:10001";
    // String decKeyIns =emaulCrypto.decryptVoteKey(EmaulCrypto.KEY_VOTE_ENC,"1111aaaa!@","1111aaaa!@","1111aaaa!@",encKey);

    // System.out.println("TEST : " + decKeyIns);
    // System.out.println("TEST : " +
    // "cddd08280bc47f7fba3e1c94c69a5267d7724056452704b95247b1e1d1140a02d02036905e8a77e0fa6f9b106aeb3c7ddc4ab8d225d80a40b97f708f8e181d02174ac479bd23b7ee856681cde2597ed6148456a5f9a9c186c6afcf23e08a8ff8fdf1d18a58de2a4fa91507fe37c99d0767f626cb218ec2d0c5c4d7fc33b6912e7ed396f1fa8f3150a52ca6765c97a0b15d7ccb811e043eb0e0752ca95d42ee76f68671ba1daa08c7a922e4d258c18f0dccf7b304152b6b29a8dcd1587ee80f27aa2d8d763cc394a04b84d597a4539c5b7c5a44b629a69f6f3be9dd57b4700116b0c910deb7eaac92c9e38620f6cec7f6d3c0f0a2263fd9c2ccbf2408b41d1e23:373192b541ea6e10adbb676850132212eb459b9045bc1f5d1c7d5268ebe8868298907a709c985d69b95b60e4b6f5d8e319c402e77cf3fe53ba283be291434fcbb6b8fa1275ae74eb0363275c2edab5d1e9f04d56f4a819d5ebc149352247a4302acdc1d368de2fd3e9ce54b548f9ca1b29396acf3d710c347341c272c0deaaefa3998586cac3ae5a6c0b003636864a05a4f69c75f735fd4361d81862b4caea410b4e8a5a188370f75205af292d7eba3c72be66abfa7e247758c31f108dbbcaea778affa9b4188da1bdd5c7d135205dfadda2b7185df5c68fb580e4660073bd5d3cc8364255c15a663e56bd27600c3b3a66881b7605dbb5f849ef9ed16a769b21");
    // decKeyIns =
    // "cddd08280bc47f7fba3e1c94c69a5267d7724056452704b95247b1e1d1140a02d02036905e8a77e0fa6f9b106aeb3c7ddc4ab8d225d80a40b97f708f8e181d02174ac479bd23b7ee856681cde2597ed6148456a5f9a9c186c6afcf23e08a8ff8fdf1d18a58de2a4fa91507fe37c99d0767f626cb218ec2d0c5c4d7fc33b6912e7ed396f1fa8f3150a52ca6765c97a0b15d7ccb811e043eb0e0752ca95d42ee76f68671ba1daa08c7a922e4d258c18f0dccf7b304152b6b29a8dcd1587ee80f27aa2d8d763cc394a04b84d597a4539c5b7c5a44b629a69f6f3be9dd57b4700116b0c910deb7eaac92c9e38620f6cec7f6d3c0f0a2263fd9c2ccbf2408b41d1e23:373192b541ea6e10adbb676850132212eb459b9045bc1f5d1c7d5268ebe8868298907a709c985d69b95b60e4b6f5d8e319c402e77cf3fe53ba283be291434fcbb6b8fa1275ae74eb0363275c2edab5d1e9f04d56f4a819d5ebc149352247a4302acdc1d368de2fd3e9ce54b548f9ca1b29396acf3d710c347341c272c0deaaefa3998586cac3ae5a6c0b003636864a05a4f69c75f735fd4361d81862b4caea410b4e8a5a188370f75205af292d7eba3c72be66abfa7e247758c31f108dbbcaea778affa9b4188da1bdd5c7d135205dfadda2b7185df5c68fb580e4660073bd5d3cc8364255c15a663e56bd27600c3b3a66881b7605dbb5f849ef9ed16a769b21";
    // String encryptedDataFileName = "C:\\dev\\test.emaul";
    // Item item2 = voteCrypto.decryptInsp(decKeyIns, encryptedDataFileName);
    // System.out.println(item2.toString());
    //
    // FileOutputStream os = new FileOutputStream(new File("C:\\dev\\images.jpg"));
    // os.write(item2.signData);
    // os.flush();
    // os.close();
    // }
}
