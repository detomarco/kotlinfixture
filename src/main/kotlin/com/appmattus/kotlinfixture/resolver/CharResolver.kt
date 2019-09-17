package com.appmattus.kotlinfixture.resolver

import com.appmattus.kotlinfixture.Unresolved
import kotlin.random.Random

class CharResolver : Resolver {

    override fun resolve(obj: Any?, resolver: Resolver): Any? =
        if (obj == Char::class) (Random.nextInt(26) + 'a'.toInt()).toChar() else Unresolved
}