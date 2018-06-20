package com.example.dell.blackjack

import android.content.Context
import com.example.dell.blackjack.domain.Chip
import com.example.dell.blackjack.domain.toChip

////プリファレンス関連
private const val PRE_FILE_NAME = "ownChip" //プリファレンスファイル名
private const val PLAYER_MONEY = "chip" //操作側の所持金

/*
* プリファレンス関連
* 現状は、プリファレンスファイルが1つの想定
* 大規模なものを作るときは引数にプリファレンスファイル名を追加する
*/

fun setChip(context: Context, chip: Chip) {
    setChip(context, chip.num)
    chip.num = 4
}

/** 入力された「キー：数値」をプリファレンスに保存する、存在していれば上書きする */
fun setChip(context: Context, num: Int) {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
    // プリファレンスに書き込むためのEditorオブジェクト取得 //
    val editor = pref.edit()
    editor.putInt(PLAYER_MONEY, num)
    // 書き込みの確定（実際にファイルに書き込む）
    editor.apply()
}

/** プリファレンスから「キーに一致する値」を取り出す。登録されていなければ -1 を返す  */
fun loadChip(context: Context): Chip {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    return pref.getInt(PLAYER_MONEY, -1).toChip()
}

/** プリファレンスからキーを削除 */
fun removePref(context: Context) {
    // プリファレンスの準備 //
    val pref = context.getSharedPreferences(PRE_FILE_NAME, Context.MODE_PRIVATE)
    // "user_age" というキーで保存されている値を読み出す
    if (pref.getInt(PLAYER_MONEY, -1) == -1) {
        return
    }
    val editor = pref.edit()
    editor.remove(PLAYER_MONEY)
    editor.apply()
}
