Kanji Damerau Levenshtein Distance
====

Edit distance to fix OCR kanji errors, an extended Damerau Levenshtein Distance that includes kanji radicals into a part of the calculation.

## Description

Given the large number of kanji characters, OCR systems often make errors, such as 往文書, whereas 注文書 is provided as the ground truth. These types of errors can be corrected more precisely by considering kanji radicals.

Traditional edit distance approaches treat a kanji character in the same way as an alphabet character. For example, these approaches measure the distance between 往文書 and 注文書 as same as the distance between 往文書 and 公文書.

In contrary, Kanji Damerau Levenshtein Distance disassembles a kanji character into radicals so as to measure the distance between 往文書 and 注文書 closer. This idea has been implemented because the 彳 differs from 氵 less than 往 from 公.
