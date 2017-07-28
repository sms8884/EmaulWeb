var jsonData = {
    "root" : {
        "bankSample" : {
            "description" : "",
            "gridData" : {
                "grid" : [
                    {
                        "date" : "20040301",
                        "in" : "2500000",
                        "name" : "Steve",
                        "num" : "BANK001",
                        "out" : "0",
                        "total" : "2500000"
                    },
                    {
                        "date" : "20040302",
                        "in" : "500000",
                        "name" : "David",
                        "num" : "BANK001",
                        "out" : "0",
                        "total" : "500000"
                    },
                    {
                        "date" : "20040302",
                        "in" : "0",
                        "name" : "Frank",
                        "num" : "BANK001",
                        "out" : "100000",
                        "total" : "-100000"
                    },
                    {
                        "date" : "20040315",
                        "in" : "150000",
                        "name" : "Michael",
                        "num" : "BANK002",
                        "out" : "0",
                        "total" : "150000"
                    },
                    {
                        "date" : "20040315",
                        "in" : "1000",
                        "name" : "Steve",
                        "num" : "BANK002",
                        "out" : "0",
                        "total" : "1000"
                    },
                    {
                        "date" : "20040328",
                        "in" : "10000",
                        "name" : "Nick",
                        "num" : "BANK003",
                        "out" : "20000",
                        "total" : "-10000"
                    },
                    {
                        "date" : "20040415",
                        "in" : "1000",
                        "name" : "Tady",
                        "num" : "BANK003",
                        "out" : "0",
                        "total" : "1000"
                    },
                    {
                        "date" : "20040421",
                        "in" : "100000",
                        "name" : "Han",
                        "num" : "BANK004",
                        "out" : "0",
                        "total" : "100000"
                    },
                    {
                        "date" : "20040421",
                        "in" : "1000",
                        "name" : "Kim",
                        "num" : "BANK004",
                        "out" : "0",
                        "total" : "1000"
                    },
                    {
                        "date" : "20040428",
                        "in" : "10000",
                        "name" : "Won",
                        "num" : "BANK005",
                        "out" : "20000",
                        "total" : "-10000"
                    },
                    {
                        "date" : "20040504",
                        "in" : "1000",
                        "name" : "Sun",
                        "num" : "BANK005",
                        "out" : "0",
                        "total" : "1000"
                    },
                    {
                        "date" : "20040511",
                        "in" : "1000000",
                        "name" : "Will",
                        "num" : "BANK005",
                        "out" : "20000",
                        "total" : "980000"
                    },
                    {
                        "date" : "20040515",
                        "in" : "1000",
                        "name" : "Hans",
                        "num" : "BANK006",
                        "out" : "0",
                        "total" : "1000"
                    }
                ]
            },
            "multitotal" : "true"
        }
    }
};

var xmlData = "\<root\>\<bankSample\>\<gridData\>\<grid\>\<num\>BANK001\<\/num\>\<name\>Steve\<\/name\>\<date\>20040301\<\/date\>\<in\>2500000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK001\<\/num\>\<name\>David\<\/name\>\<date\>20040302\<\/date\>\<in\>500000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK001\<\/num\>\<name\>Frank\<\/name\>\<date\>20040302\<\/date\>\<in\>0\<\/in\>\<out\>100000\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK002\<\/num\>\<name\>Michael\<\/name\>\<date\>20040315\<\/date\>\<in\>150000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK002\<\/num\>\<name\>Steve\<\/name\>\<date\>20040315\<\/date\>\<in\>1000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK003\<\/num\>\<name\>Nick\<\/name\>\<date\>20040328\<\/date\>\<in\>10000\<\/in\>\<out\>20000\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK003\<\/num\>\<name\>Tady\<\/name\>\<date\>20040415\<\/date\>\<in\>1000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK004\<\/num\>\<name\>Han\<\/name\>\<date\>20040421\<\/date\>\<in\>100000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK004\<\/num\>\<name\>Kim\<\/name\>\<date\>20040421\<\/date\>\<in\>1000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK005\<\/num\>\<name\>Won\<\/name\>\<date\>20040428\<\/date\>\<in\>10000\<\/in\>\<out\>20000\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK005\<\/num\>\<name\>Sun\<\/name\>\<date\>20040504\<\/date\>\<in\>1000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK005\<\/num\>\<name\>Will\<\/name\>\<date\>20040511\<\/date\>\<in\>1000000\<\/in\>\<out\>20000\<\/out\>\<total\/\>\<\/grid\>\<grid\>\<num\>BANK006\<\/num\>\<name\>Hans\<\/name\>\<date\>20040515\<\/date\>\<in\>1000\<\/in\>\<out\>0\<\/out\>\<total\/\>\<\/grid\>\<\/gridData\>\<description\/\>\<multitotal\>true\<\/multitotal\>\<\/bankSample\>\<\/root\>";