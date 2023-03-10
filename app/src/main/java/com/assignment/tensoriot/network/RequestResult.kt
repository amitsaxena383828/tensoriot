package com.assignment.tensoriot.network

open class RequestResult<out T> private constructor(
        val status: Status,
        val data: T?,
        val message: String?
) {

    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T): RequestResult<T> {
            return RequestResult(
                    Status.SUCCESS,
                    data,
                    null
            )
        }

        fun <T> success(msg: String? = null, data: T): RequestResult<T> {
            return RequestResult(
                    Status.SUCCESS,
                    data,
                    msg
            )
        }

        fun <T> error(msg: String? = null, data: T? = null): RequestResult<T> {
            return RequestResult(
                    Status.ERROR,
                    data,
                    msg
            )
        }

        fun <T> loading(data: T? = null): RequestResult<T> {
            return RequestResult(
                    Status.LOADING,
                    data,
                    null
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestResult<*>

        if (status != other.status) return false
        if (data != other.data) return false
        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + (message?.hashCode() ?: 0)
        return result
    }
}