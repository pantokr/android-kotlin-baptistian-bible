package com.panto.bible.data.local

import androidx.compose.ui.graphics.Color

object BibleConstant {
    val TAG: String = "BIBLE-LOG"

    val VERSION_LIST = arrayOf("han", "gae", "kjv", "niv") // 업로드
    val VERSION_LIST_KOR = arrayOf("개역한글", "개역개정", "KJV", "NIV") // 한국어
    val LANGUAGE_LIST = arrayOf(arrayOf(0, 1), arrayOf(2, 3)) // [0] -> Kor, [1] -> Eng

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
        "요한1서",
        "요한2서",
        "요한3서",
        "유다서",
        "요한계시록"
    )

    val BOOK_LIST_KOR_SHORT = arrayOf(
        "창",
        "출",
        "레",
        "민",
        "신",
        "수",
        "삿",
        "룻",
        "삼상",
        "삼하",
        "왕상",
        "왕하",
        "대상",
        "대하",
        "스",
        "느",
        "에",
        "욥",
        "시",
        "잠",
        "전",
        "아",
        "사",
        "렘",
        "애",
        "겔",
        "단",
        "호",
        "욜",
        "암",
        "옵",
        "욘",
        "미",
        "나",
        "합",
        "습",
        "학",
        "슥",
        "말",
        "마",
        "막",
        "눅",
        "요",
        "행",
        "롬",
        "고전",
        "고후",
        "갈",
        "엡",
        "빌",
        "골",
        "살전",
        "살후",
        "딤전",
        "딤후",
        "딛",
        "몬",
        "히",
        "약",
        "벧전",
        "벧후",
        "요일",
        "요이",
        "요삼",
        "유",
        "계"
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
        50, 40, 27, 36, 34, 24, 21, 4, 31, 24,
        22, 25, 29, 36, 10, 13, 10, 42, 150, 31,
        12, 8, 66, 52, 5, 48, 12, 14, 3,
        9, 1, 4, 7, 3, 3, 3, 2, 14, 4,

        28, 16, 24, 21, 28, 16, 16, 13, 6, 6,
        4, 4, 5, 3, 6, 4, 3, 1,
        13, 5, 5, 3, 5, 1, 1, 1, 22
    )

    val BOOK_CHAPTER_COUNT_SUM_LIST = arrayOf(
        0, 50, 90, 117, 153, 187, 211, 232, 236, 267, 291,
        313, 338, 367, 403, 413, 426, 436, 478, 628,
        659, 671, 679, 745, 797, 802, 850, 862, 876, 879,
        888, 889, 893, 900, 903, 906, 909, 911, 925, 929,

        957, 973, 997, 1018, 1046, 1062, 1078, 1091, 1097, 1103,
        1107, 1111, 1116, 1119, 1125, 1129, 1132, 1133,
        1146, 1151, 1156, 1159, 1164, 1165, 1166, 1167
    )

    val SAVE_COLORS = arrayOf(
        Color(0xFFEA3323),
        Color(0xFFFFBF00),
        Color(0xFF75FB4C),
        Color(0xFF69ABFF),
    )
}
