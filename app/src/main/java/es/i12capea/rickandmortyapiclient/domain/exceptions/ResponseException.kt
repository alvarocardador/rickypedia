package es.i12capea.rickandmortyapiclient.domain.exceptions

data class ResponseException(val code: Int, val desc: String) : Throwable() {
}