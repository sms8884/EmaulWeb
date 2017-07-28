// 구글 플레이 이마을앱 다운로드 URL
var GOOGLE_PLAY_URL = "https://itunes.apple.com/kr/app/ima-eul-mobail-tupyo-mich/id1058747039?mt=8";
// 구글 플레이 이마을앱 다운로드 URL
var APP_STORE_URL = "https://play.google.com/store/apps/details?id=com.jaha.app.emaul";

// 공지사항, 주민게시판 URI
var EMAUL_APP_POST_URI = "emaul://post-detail?id=%postId";
// 이벤트 게시판 URI
var EMAUL_APP_EVENT_URI = "emaul://post-event-detail?id=%postId";
// 마을뉴스 URI
var EMAUL_APP_MAULNEWS_URI = "emaul://today-detail?id=%postId&newsCategory=%newsCategory";

// 핸드폰번호 정규표현식
var HP_REGEXP = /^(?:(010-\d{4})|(01[1|6|7|8|9]-\d{3,4}))-(\d{4})$/
// 일반전화번호 정규표현식
var PHONE_REGEXP = /^\d{2,3}-\d{3,4}-\d{4}$/;
// 이메일 정규표현식
var EMAIL_REGEXP = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
// IP 정규표현식
var IP_REGEXP = /^([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\.([1-9]?[0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])){3}$/;

/**
 * 유닉스 타임스태프 형식의 날짜(숫자)를 변환하여 반환한다.
 */
function getDateFromUnixTimestamp(date) {
	var d = new Date(date);
	var year = d.getFullYear();
	var month = d.getMonth();
	var month = month + 1;
	var m = month<10?("0"+month):month;
	var day = d.getDate();
	var da = day<10?("0"+day):day;
	var hour = d.getHours();
	var h = hour<10?("0"+hour):hour;
	var minutes = d.getMinutes();
	var mi = minutes<10?("0"+minutes):minutes;
	var seconds = d.getSeconds();
	var s = seconds<10?("0"+seconds):seconds;

	var cvtDate = year + "-" + m + "-" + da + " " + h + ":" + mi + ":" + s;

	return cvtDate;
};
/**
 * UI에서 목록의 시작 번호를 반환한다.
 */
function getStartNoOfList(pageNum, pageSize, totalRecordCount) {
	// console.log("* pageNum: " + pageNum + ", pageSize: " + pageSize + ",
	// totalRecordCount: " + totalRecordCount);
	return totalRecordCount-(pageSize*(pageNum-1));
};
/**
 * 특수문자 제거
 */
function removeSpecialChars(param) {
	var specialChars = /[-~!#$^&*=+|:;?"<,.>']/;
	return param.split(specialChars).join('');
};
/**
 * 현재시간을 yyyyMMddHHmmss형식으로 반환
 */
function getNowDate() {
/**
	var d = new Date();
	var year = d.getFullYear();
	var month = d.getMonth();
	var month = month + 1;
	var m = month<10?("0"+month):month;
	var day = d.getDate();
	var da = day<10?("0"+day):day;
	var hour = d.getHours();
	var h = hour<10?("0"+hour):hour;
	var minutes = d.getMinutes();
	var mi = minutes<10?("0"+minutes):minutes;
	var seconds = d.getSeconds();
	var s = seconds<10?("0"+seconds):seconds;

	return year + m + da + h + mi + s;
*/
	return new Date().format("yyyyMMddHHmmss");
};
/**
 * yyyy-MM-dd형식을 Date로 반환
 */
function gfnSetFormatDate(baseDate) {
	var arrBaseDate = baseDate.split("-");
    var year = arrBaseDate[0];
    var month = parseInt(arrBaseDate[1]) - 1;
    var day = parseInt(arrBaseDate[2]);

    var d = new Date();
    d.setFullYear(year);
    d.setMonth(month);
    d.setDate(day);

    return d;
}

/**
 * GCM 발송
 *
 * @param gcmSendUrl
 * @param jsonData
 */
function sendGcm(gcmSendUrl, jsonData) {
	if (!jsonData) {
		alert(" JSON 데이타를 전송해주세요. ");
		return;
	}
	if (!jsonData.type) {
		alert(" type은 필수입니다. ");
		return;
	}
	if (!jsonData.action) {
		alert(" action은 필수입니다. ");
		return;
	}
	if (!jsonData.title) {
		alert(" title은 필수입니다. ");
		return;
	}
	if (!jsonData.message) {
		alert(" message는 필수입니다. ");
		return;
	}
	if (jsonData.isTargetAll !== true) {
		jsonData.isTargetAll = false;
	}
	/**
	 * if (!jsonData.target) { alert(" target는 필수입니다. "); return; }
	 */

    var reqData = {};
    reqData.type = jsonData.type; // "action"
    reqData.action = jsonData.action; // 화면이동(email://aptfee)
    reqData.title = jsonData.title; // 제목
    reqData.message = jsonData.message; // 본문
    reqData.isTargetAll = jsonData.isTargetAll; // true/false 전체발송여부
    reqData.target = jsonData.target;

    $.ajax({
        url: gcmSendUrl
        , type: 'post'
        , contentType: "application/json; charset=utf-8"
        , dataType: "json"
        , data: JSON.stringify(reqData)
        , success: function (data) {
            if (console && console.log) console.log(data);

            if(data.result == "success"){
            	if (console && console.log) console.log('전송되었습니다!');
            }else if(data.result == 'empty-target'){
            	if (console && console.log) console.log('전송 대상이 없습니다.');
            }
        }
        , error: function(xhr, status, error) {
        	if (console && console.log) console.log("error : " + error.message);
        }
    });
};
/**
 * 숫자만 허용
 *
 * @param event
 * @returns {Boolean}
 */
function onlyNumber(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( (keyID >= 48 && keyID <= 57) || (keyID >= 96 && keyID <= 105) || keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 || keyID == 9 )	// 9 == tabkey
		return;
	else
		return false;
};
/**
 * 숫자 아닌 문자 제거
 *
 * @param event
 */
function removeChar(event) {
	event = event || window.event;
	var keyID = (event.which) ? event.which : event.keyCode;
	if ( keyID == 8 || keyID == 46 || keyID == 37 || keyID == 39 )
		return;
	else
		event.target.value = event.target.value.replace(/[^0-9]/g, "");
};

/**
 * 숫자만허용 & 문자제거 이벤트 한번에 등록
 */
$.fn.setNumber = function(){
	$(this).on("keydown", onlyNumber);
    $(this).on("keyup", removeChar);
}

/**
 * jquery 이용한 엔터시 해당 function 호출
 */
$.fn.setEnter = function(func){
	$(this).on('keyup', function(e) {
		if (e.which == 13) {
			func();
		}
	});
}

/**
 * null undefined "" check
 */
function gfnIsEmpty(val){
	if( val == null || typeof val == "undefined" || val == "" ) {
		return true;
	}
	return false;
}

/**
 * string byte
 */
function gfnByte(str){
    var size = 0;
    if(str != null && str != undefined && str != "undefined" && str != ""){
        for(var i=0,len=str.length;i<len;i++) {
            size++;
            if(44032 <str.charCodeAt(i) && str.charCodeAt(i) <=  55203) {
                size++;
            }
            if(12593 <= str.charCodeAt(i) && str.charCodeAt(i) <= 12686 ) {
                size++;
            }
        }
    }
    return size;
}

/**
 * jquery 이용한 bootstrap vertical middle open
 */
$.fn.centerModal = function(options){
	$(this).on('show.bs.modal', reposition);
    // Reposition when the window is resized
    $(window).on('resize', function() {
        $('#modal-addressType:visible').each(reposition);
    });
    $(this).modal(options);
}
function reposition() {
    var modal = $(this),
        dialog = modal.find('.modal-dialog');
    modal.css('display', 'block');
    dialog.css("margin-top", Math.max(0, ($(window).height() - dialog.height()) / 2));
}



/**
 * 로딩이미지와 투명 dimmed 처리
 */
function gfnLoadingImage(func) {
	$.blockUI({ message: "<img src='/img/loading.gif' />"
		, css: { textAlign: "center"
			, border: "none"
			, backgroundColor: ""
			, color: ""
		}
	    , overlayCSS:  {
	    	backgroundColor: ""
	    	, opacity: ""
	    	, cursor: "wait"
	    }
	    , onBlock: function() {
	    	if (func) {
	    		func.call();
	    	}
	    }
	});
}
/**
 * dimmed 해체
 */
function gfnUnLoadingImage() {
	$.unblockUI();
}



/**
 * isEmpty
 * 2016.10.17 (@cyt)
 */
//Speed up calls to hasOwnProperty
var hasOwnProperty = Object.prototype.hasOwnProperty;
function isEmpty(obj) {

    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    // If it isn't an object at this point
    // it is empty, but it can't be anything *but* empty
    // Is it empty?  Depends on your application.
    if (typeof obj !== "object") return true;

    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
}
/* Not isEmpty */
function isNotEmpty(obj) {
	return !isEmpty(obj);
}



/**
 * window.open
 * 2016.10.17 (@cyt)
 */
function openWindow(url, name, width, height, left, top) {

	if (!width) {
		width = 800;
	}
	if (!height) {
		height = 600;
	}
	if (!left) {
		left = (screen.width-width)/2;
	}
	if (!top) {
		top = (screen.height-height)/2;
	}
	if (!name) {
		name = "_blank";
	}
	//console.log("width : " + width + "/ height : " + height + "/ left : " + left + "/ top : " + top + "/ name : " + name);
	window.open(url,name,"width="+width+", height="+height+", left="+left+", top="+top+", toolbar=no, location=no, status=no, menubar=no, scrollbars=no, resizable=no");
}



/**
 * obj : PagingHeler Object to json
 * methodName : execute function
 * return div inner string
 * Ref : vote-result.html
 */
// 비동기 페이징 디스플레이
function makePaging(obj, methodName) {

	var pagingHtml = '';
	var pagingMethod = '';
	if (methodName == 'voteOff') {
		pagingMethod = 'selectOfflineVoterList';
	} else if (methodName == 'voteOn') {
		pagingMethod = 'selectVoterList';
	} else {
		pagingMethod = methodName;
	}

	pagingHtml += '<ul class="pagination">';
	pagingHtml += '<li class="' + (obj.pageNum == 1 ? 'disabled' : '') + '">';
	if (obj.pageNum > 1) {
		pagingHtml += '<a href="javascript:' + pagingMethod + '(1);">맨처음</a>';
	} else {
		pagingHtml += '<span>처음</span>';
	}
	pagingHtml += '</li>';

	pagingHtml += '<li class="' + (obj.previousPage ? '' : 'disabled') + '">';
	if (obj.previousPage) {
		pagingHtml += '<a href="javascript:' + pagingMethod + '(' + (obj.pageNum - 1) + ');"  title="Go to previous page">«</a>';
	} else {
		pagingHtml += '<span>«</span>';
	}
	pagingHtml += '</li>';

	for (var i = obj.startPageBlockNum; i <= obj.endPageBlockNum; i++) {
		pagingHtml += '<li class="' + (i == obj.pageNum ? 'active' : '') + '">';
		if (i == obj.pageNum) {
			pagingHtml += '<span>' + i + '</span>';
			} else {
				pagingHtml += '<a href="javascript:' + pagingMethod + '(' + i + ');"  >' + i + '</a>';
			}
		pagingHtml += '</li>';
	}

	pagingHtml += '<li class="' + (obj.nextPage ? '' : 'disabled') + '">';
	if (obj.nextPage) {
		pagingHtml += '<a href="javascript:' + pagingMethod + '(' + (obj.pageNum + 1) + ');"  title="Go to next page">»</a>';
	} else {
		pagingHtml += '<span>»</span>';
	}
	pagingHtml += '</li>';

	pagingHtml += '<li class="' + (obj.pageNum == obj.totalPageNum ? 'disabled' : '') + '">';
	if (obj.pageNum == obj.totalPageNum) {
		pagingHtml += '<span>마지막</span>';
	} else {
		pagingHtml += '<a href="javascript:' + pagingMethod + '(' + obj.totalPageNum + ');">끝</a>';
	}
	pagingHtml += '</li>';

	return pagingHtml;
}




/**
 * 날자 포멧관련 공통화 위치 이동 (html inner to common.js)
 */

Date.prototype.yyyymmdd = function () {
    var yyyy = this.getFullYear().toString();
    var MM = (this.getMonth() + 1).toString(); // getMonth() is zero-based
    var dd = this.getDate().toString();
    var hh = this.getHours().toString();
    var mm = this.getMinutes().toString();
    return yyyy + '년 ' + (MM[1] ? MM : "0" + MM[0]) + '월 ' + (dd[1] ? dd : "0" + dd[0]) + '일 ' + (hh[1] ? hh : "0" + hh[0]) + ':' + (mm[1] ? mm : "0" + mm[0]); // padding
};

Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";

    var weekName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var d = this;

    return f.replace(/(yyyy|yy|MM|dd|E|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy": return d.getFullYear();
            case "yy": return (d.getFullYear() % 1000).zf(2);
            case "MM": return (d.getMonth() + 1).zf(2);
            case "dd": return d.getDate().zf(2);
            case "E": return weekName[d.getDay()];
            case "HH": return d.getHours().zf(2);
            case "hh": return ((h = d.getHours() % 12) ? h : 12).zf(2);
            case "mm": return d.getMinutes().zf(2);
            case "ss": return d.getSeconds().zf(2);
            case "a/p": return d.getHours() < 12 ? "오전" : "오후";
            default: return $1;
        }
    });
};

String.prototype.string = function(len){var s = '', i = 0; while (i++ < len) { s += this; } return s;};
String.prototype.zf = function(len){return "0".string(len - this.length) + this;};
Number.prototype.zf = function(len){return this.toString().zf(len);};


/**
 * HTML 태그 제거
 *
 * @param text
 * @returns
 */
function removeHtml(text) {
    text = text.replace(/<br>/ig, "\n"); // <br>을 엔터로 변경
    text = text.replace(/&nbsp;/ig, " "); // 공백
    // HTML 태그제거
    text = text.replace(/<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig, "");

    // shkim.add.
    text = text.replace(/<(no)?script[^>]*>.*?<\/(no)?script>/ig, "");
    text = text.replace(/<style[^>]*>.*<\/style>/ig, "");
    text = text.replace(/<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>/ig, "");
    text = text.replace(/<\\w+\\s+[^<]*\\s*>/ig, "");
    text = text.replace(/&[^;]+;/ig, "");
    text = text.replace(/\\s\\s+/ig, "");

    return text;
}
/**
 * 브라우저 버전을 반환한다. IE가 아닌 경우는 99를 반환한다.
 */
function getBrowserVersion() {
	var word;
	var version = 99;

	var agent = navigator.userAgent.toLowerCase();
	var name = navigator.appName;

	// IE old version ( IE 10 or Lower )
	if ( name == "Microsoft Internet Explorer" ) {
		word = "msie ";
	}
	else {
		// IE 11
		if ( agent.search("trident") > -1 ) {
			word = "trident/.*rv:";
		}
		else if ( agent.search("edge/") > -1 ) {
			word = "edge/"; // Microsoft Edge
		}
	}

	var reg = new RegExp( word + "([0-9]{1,})(\\.{0,}[0-9]{0,1})" );

	if ( reg.exec( agent ) != null  ) {
		version = RegExp.$1 + RegExp.$2;

		if (version.indexOf('.') > -1) {
			var temps = version.split(".", -1);
			// alert(temps[0]);
			version = temps[0];
		}
	}

	return version;
}
/**
 * lpad
 */
function gfnLpad(originalstr, length, strToPad) {

    while (originalstr.toString().length < length)

        originalstr = strToPad + originalstr;

    return originalstr;
}

/**
 * null value check and set "" or default value
 * @param ori
 * @param def
 * @returns
 */
function gfnNvl(ori, def){
	if(gfnIsEmpty(ori) == true){
		if(gfnIsEmpty(def) == true){
			return "";
		} else {
			return def;
		}
	} else {
		return ori;
	}
}

/**
 * 숫자에 컴마 넣기
 *
 * @param x
 * @returns
 */
function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

/**
 * 안드로이드/IOS에 따라 이마을 앱이 설치되어 있으면 이마을앱 내 해당 화면으로 이동하고 아니면 스토어로 이동
 *
 * @param appUri
 */
function moveStoreOrOpenApp(appUri) {
	var openAt = new Date();
	var uagentLow = navigator.userAgent.toLocaleLowerCase();
	var chrome25;
	var kitkatWebview;

	$("body").append("<iframe id='____emaulapp-link____'></iframe>");
	$("#____emaulapp-link____").hide();

	try {
		setTimeout(function() {
			if (new Date() - openAt < 4000) {
				if (uagentLow.search("android") > -1) {
					$("#____emaulapp-link____").attr("src", GOOGLE_PLAY_URL);
				} else if (uagentLow.search("iphone") > -1) {
					window.location.href = APP_STORE_URL;
				}
			}
		}, 1000);

		if (uagentLow.search("android") > -1) {
			chrome25 = uagentLow.search("chrome") > -1 && navigator.appVersion.match(/Chrome\/\d+.\d+/)[0].split("/")[1] > 25;
			kitkatWebview = uagentLow.indexOf("naver") != -1 || uagentLow.indexOf("daum") != -1;

			if (chrome25 && !kitkatWebview) {
				$("#_btn_open_emaulapp").show();
				$("#_btn_open_emaulapp").on("click", function() {
					// $("#____emaulapp-link____").attr("src", appUri);
					// window.location.href = appUri;

					window.location.href = appUri + '#Intent;scheme=emaul;action=android.intent.action.VIEW;category=android.intent.category.BROWSABLE;package=com.jaha.app.emaul;end'; // 이마을앱 화면으로 이동하는 경우
				});
			} else {
				$("#____emaulapp-link____").attr("src", appUri); // 이마을앱 화면으로 이동하는 경우
			}
		} else if (uagentLow.search("iphone") > -1) {
			$("#____emaulapp-link____").attr("src", appUri); // 이마을앱 화면으로 이동하는 경우
			// document.location.href= 'emaul://post-event-detail?id='; // IOS9, 이마을앱 화면으로 이동하는 경우
		}
	}
	catch(e) {
		alert(e);
	}
}
function gfnComma(num){
	return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function gfnUnComma(num){
	return num.toString().replace(/[^\d]+/g, "");
}

/**
 * Ajax 파일 다운로드
 * 예) $.download('/jaha/apt/ap/access/log/list/excel-download', param, 'post');
 * 주의) data에 공백이 있는 경우 +로 치환된다.
 */
jQuery.download = function(url, data, method) {
    // url과 data를 입력받음
    if (url && data) {
        // data는  string 또는 array/object 를 파라미터로 받는다.
        data = typeof data == 'string' ? data : jQuery.param(data);
        // 파라미터를 form의  input으로 만든다.
        var inputs = '';
        jQuery.each(data.split('&'), function(){
            var pair = this.split('=');
            inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
            // console.log(inputs);
        });
        // request를 보낸다.
        jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>').appendTo('body').submit().remove();
    };
};

/**
 * 아파트 선택 공통 팝업
 * 팝업띄운 부모창에서 function  _searchAptCallback() 를 구현해야함.
 * opt { multi : true & false, id : popup id }
 */
function gfnAptSearchPopup(opt){
	var width = 600;
    var height = 720;

    var param = "";
    var popupName = "aptSearchPopup";
    if(isNotEmpty(opt)){
    	if(opt.multi == true){
    		param = "?mode=multi";
    	}
    	if(isNotEmpty(opt.name)){
    		popupName = opt.name;
    	}
    }
    openWindow("/apt/search/popup"+ param, popupName, width, height);
}
