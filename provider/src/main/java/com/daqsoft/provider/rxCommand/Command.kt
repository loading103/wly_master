package com.daqsoft.provider.rxCommand

import io.reactivex.annotations.NonNull
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer

/**
 * 单参数应答
 */
interface ResponseCommand<T> : Consumer<T> {
    override fun accept(@NonNull obj: T)
}

/**
 * 不带参数应答
 */
interface ReplyCommand : Action {
    override fun run()
}