package com.example.dell.blackjack.domain


sealed class Score {
    override fun toString(): String {
        return this.num.toString()
    }

    operator fun compareTo(other: Score): Int {
        return when {
            this === BlackJack -> {
                if (other === BlackJack) {
                    0
                } else {
                    1
                }

            }
            this is Bust -> {
                if (other is Bust) {
                    0
                } else {
                    -1
                }
            }
            this is Point -> {
                return when {
                    other === BlackJack -> -1
                    other is Bust -> 1
                    other is Point -> this.num.compareTo(other.num)
                    else -> throw IllegalArgumentException()
                }
            }
            else -> throw IllegalArgumentException()
        }
    }

    abstract val num: Int


    object BlackJack : Score() {
        override val num: Int
            get() = 21
    }

    class Bust(override val num: Int) : Score()

    class Point(override val num: Int) : Score()

}
