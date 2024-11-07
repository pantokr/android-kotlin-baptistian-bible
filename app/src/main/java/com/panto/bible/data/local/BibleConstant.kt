package com.panto.bible.data.local

object BibleConstant {
    val TAG:String = "BIBLE-LOG"

    val VERSION_LIST = arrayOf("han", "gae")
    val VERSION_LIST_KOR = arrayOf("개역한글", "개역개정", "KJV")

    val VERSE_COUNT_LIST = arrayOf(31080, 31088)

    val BOOK_LIST_KOR = arrayOf(
        "창세기",
        "출애굽기",
        "레위기",
        "민수기",
        "신명기",
        "여호수아",
        "사사기",
        "룻기",
        "사무엘상",
        "사무엘하",
        "열왕기상",
        "열왕기하",
        "역대상",
        "역대하",
        "에스라",
        "느헤미야",
        "에스더",
        "욥기",
        "시편",
        "잠언",
        "전도서",
        "아가",
        "이사야",
        "예레미야",
        "예레미야애가",
        "에스겔",
        "다니엘",
        "호세아",
        "요엘",
        "아모스",
        "오바댜",
        "요나",
        "미가",
        "나훔",
        "하박국",
        "스바냐",
        "학개",
        "스가랴",
        "말라기",
        "마태복음",
        "마가복음",
        "누가복음",
        "요한복음",
        "사도행전",
        "로마서",
        "고린도전서",
        "고린도후서",
        "갈라디아서",
        "에베소서",
        "빌립보서",
        "골로새서",
        "데살로니가전서",
        "데살로니가후서",
        "디모데전서",
        "디모데후서",
        "디도서",
        "빌레몬서",
        "히브리서",
        "야고보서",
        "베드로전서",
        "베드로후서",
        "요한일서",
        "요한이서",
        "요한삼서",
        "유다서",
        "요한계시록"
    )

    val BOOK_LIST_ENG = arrayOf(
        "Genesis",  // 창세기
        "Exodus",   // 출애굽기
        "Leviticus",// 레위기
        "Numbers",  // 민수기
        "Deuteronomy", // 신명기
        "Joshua",   // 여호수아
        "Judges",   // 사사기
        "Ruth",     // 룻기
        "1 Samuel", // 사무엘상
        "2 Samuel", // 사무엘하
        "1 Kings",  // 열왕기상
        "2 Kings",  // 열왕기하
        "1 Chronicles", // 역대상
        "2 Chronicles", // 역대하
        "Ezra",     // 에스라
        "Nehemiah", // 느헤미야
        "Esther",   // 에스더
        "Job",      // 욥기
        "Psalms",   // 시편
        "Proverbs", // 잠언
        "Ecclesiastes", // 전도서
        "Song of Solomon", // 아가
        "Isaiah",   // 이사야
        "Jeremiah", // 예레미야
        "Lamentations", // 예레미야애가
        "Ezekiel",  // 에스겔
        "Daniel",   // 다니엘
        "Hosea",    // 호세아
        "Joel",     // 요엘
        "Amos",     // 아모스
        "Obadiah",  // 오바댜
        "Jonah",    // 요나
        "Micah",    // 미가
        "Nahum",    // 나훔
        "Habakkuk", // 하박국
        "Zephaniah",// 스바냐
        "Haggai",   // 학개
        "Zechariah",// 스가랴
        "Malachi",  // 말라기
        "Matthew",   // 마태복음
        "Mark",      // 마가복음
        "Luke",      // 누가복음
        "John",      // 요한복음
        "Acts",      // 사도행전
        "Romans",    // 로마서
        "1 Corinthians", // 고린도전서
        "2 Corinthians", // 고린도후서
        "Galatians", // 갈라디아서
        "Ephesians",  // 에베소서
        "Philippians", // 빌립보서
        "Colossians", // 골로새서
        "1 Thessalonians", // 데살로니가전서
        "2 Thessalonians", // 데살로니가후서
        "1 Timothy", // 디모데전서
        "2 Timothy", // 디모데후서
        "Titus",      // 디도서
        "Philemon",   // 빌레몬서
        "Hebrews",    // 히브리서
        "James",      // 야고보서
        "1 Peter",    // 베드로전서
        "2 Peter",    // 베드로후서
        "1 John",     // 요한1서
        "2 John",     // 요한2서
        "3 John",     // 요한3서
        "Jude",       // 유다서
        "Revelation"  // 요한계시록
    )

    val BOOK_LIST_ENG_SHORT = arrayOf(
        "Gen",  // 창세기
        "Exo",  // 출애굽기
        "Lev",  // 레위기
        "Num",  // 민수기
        "Deu",  // 신명기
        "Jos",  // 여호수아
        "Jud",  // 사사기
        "Ruth", // 룻기
        "1Sam", // 사무엘상
        "2Sam", // 사무엘하
        "1Kin", // 열왕기상
        "2Kin", // 열왕기하
        "1Chr", // 역대상
        "2Chr", // 역대하
        "Ezr",  // 에스라
        "Neh",  // 느헤미야
        "Est",  // 에스더
        "Job",  // 욥기
        "Psa",  // 시편
        "Pro",  // 잠언
        "Ecc",  // 전도서
        "Sof",  // 아가
        "Isa",  // 이사야
        "Jer",  // 예레미야
        "Lam",  // 예레미야애가
        "Eze",  // 에스겔
        "Dan",  // 다니엘
        "Hos",  // 호세아
        "Joe",  // 요엘
        "Amo",  // 아모스
        "Oba",  // 오바댜
        "Jon",  // 요나
        "Mic",  // 미가
        "Nah",  // 나훔
        "Hab",  // 하박국
        "Zep",  // 스바냐
        "Hag",  // 학개
        "Zec",  // 스가랴
        "Mal",  // 말라기
        "Mat",  // 마태복음
        "Mar",  // 마가복음
        "Luk",  // 누가복음
        "Joh",  // 요한복음
        "Act",  // 사도행전
        "Rom",  // 로마서
        "1Cor", // 고린도전서
        "2Cor", // 고린도후서
        "Gal",  // 갈라디아서
        "Eph",  // 에베소서
        "Phil", // 빌립보서
        "Col",  // 골로새서
        "1Thes",// 데살로니가전서
        "2Thes",// 데살로니가후서
        "1Tim", // 디모데전서
        "2Tim", // 디모데후서
        "Tit",  // 디도서
        "Phm",  // 빌레몬서
        "Heb",  // 히브리서
        "Jam",  // 야고보서
        "1Pet", // 베드로전서
        "2Pet", // 베드로후서
        "1Joh", // 요한1서
        "2Joh", // 요한2서
        "3Joh", // 요한3서
        "Jude", // 유다서
        "Rev"   // 요한계시록
    )

    val BOOK_CHAPTER_COUNT_LIST = arrayOf(
        50, // 창세기
        40, // 출애굽기
        27, // 레위기
        36, // 민수기
        34, // 신명기
        24, // 여호수아
        21, // 사사기
        4,  // 룻기
        31, // 사무엘상
        24, // 사무엘하
        22, // 열왕기상
        25, // 열왕기하
        29, // 역대상
        36, // 역대하
        10, // 에스라
        13, // 느헤미야
        10, // 에스더
        42, // 욥기
        150, // 시편
        31, // 잠언
        12, // 전도서
        8,  // 아가서
        66, // 이사야
        52, // 예레미야
        5,  // 예레미야애가
        48, // 에스겔
        12, // 다니엘
        14, // 호세아
        3,  // 요엘
        9,  // 아모스
        1,  // 오바댜
        4,  // 요나
        7,  // 미가
        3,  // 나훔
        3,  // 하박국
        2,  // 스바냐
        14, // 학개
        4,  // 스가랴
        3,  // 말라기
        28, // 마태복음
        16, // 마가복음
        24, // 누가복음
        21, // 요한복음
        28, // 사도행전
        16, // 로마서
        16, // 고린도전서
        13, // 고린도후서
        6,  // 갈라디아서
        6,  // 에베소서
        4,  // 빌립보서
        4,  // 골로새서
        5,  // 데살로니가전서
        3,  // 데살로니가후서
        6,  // 디모데전서
        4,  // 디모데후서
        3,  // 디도서
        1,  // 빌레몬서
        13, // 히브리서
        5,  // 야고보서
        5,  // 베드로전서
        3,  // 베드로후서
        5,  // 요한1서
        1,  // 요한2서
        1,  // 요한3서
        1,  // 유다서
        22  // 요한계시록
    )
}
