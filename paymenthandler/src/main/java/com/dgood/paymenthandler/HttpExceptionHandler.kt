package com.dgood.paymenthandler

import java.io.IOException

sealed class HttpException(message: String) : IOException(message)

class BadRequestException(message: String) : HttpException(message)
class UnauthorizedException(message: String) : HttpException(message)
class ForbiddenException(message: String) : HttpException(message)
class NotFoundException(message: String) : HttpException(message)
class UnprocessableEntityException(message: String) : HttpException(message)
class InternalServerErrorException(message: String) : HttpException(message)
class NotImplementedException(message: String) : HttpException(message)
class CustomNetworkException(message: String) : HttpException(message)
class CustomJsonParseException(message: String) : HttpException(message)