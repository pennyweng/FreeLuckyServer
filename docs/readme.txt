/play?id=aabc&p=09835757810
200

/category?type=1
{
    "res": [
        {
            "id": "a1",
            "title": "title",
            "desc": "desc",
            "participator": 1234,
            "funded": 30,
            "winrate": "1/50000",
            "target" : 50000,
            "img": "http://localhost"
        },
        {
            "id": "a2",
            "title": "title",
            "desc": "desc",
            "participator": $a2-0$,
            "funded": $a2-1$,
            "winrate": "1/50000",
            "target" : 50000,
            "img": "http://localhost"
        }
    ]
}

/images/a1.jpg


hset category-1 a1  """ {
            "id": "a1",
            "title": "title",
            "desc": "desc",
            "participator": 1234,
            "target" : 50000,
            "img": "http://localhost",
            "opendate" : 137438288
        } """


HINCRBY participator-1 a1 1


hset user 09835757810 t1

sadd a1 09835757810

hset history 0983575810 [ 
{
    id": "a1",
    "title": "title",
    "num":3233,
    "ts":13700032
}]

hset winlist a1 """ {
            "id": "a1",
            "title": "手機",
            "opendate" : 1377190769,
            "win_num": 99383,
            "win_phone": 0983575718,
        } """
[
    {
        "id": "a1",
        "title": "htc手機",
        "opendate": 1377190769,
        "win_num": 99383,
        "win_phone": "0983575718"
    },
    {
        "id": "a2",
        "title": "samsung手機",
        "opendate": 1377190769,
        "win_num": 99383,
        "win_phone": "0983575718"
    }
]





Android評價互助系統

1. 點選畫面上你要給評價的app
2. 輸入你的app名稱
3. 留下評價
4. 1分鐘後, 你的app會在我們首頁上, 等待下一個人給你評價

/getApps
[
    {
        "title" : "金錢補手",
        "img" : "http://localhost",
        "pkg" : "com.jookershop.moneycatch",
        "lock" : false
    },
    {
        "title" : "金錢補手",
        "img" : "http://localhost",
        "pkg" : "com.jookershop.moneycatch"
        "lock" : false
    },
    {
        "title" : "金錢補手",
        "img" : "http://localhost",
        "pkg" : "com.jookershop.moneycatch"
        "lock" : false
    }
]


/youapp?pkg=com.jookershop.moneycatch
Request =>
{
    "nickname" : "penny weng",
    "pkg" : "com.jookershop.meetyou"
}

Response =>
301
https://play.google.com/store/apps/details?id=com.jookershop.freelucky