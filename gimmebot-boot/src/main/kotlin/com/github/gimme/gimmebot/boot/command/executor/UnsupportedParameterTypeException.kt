package com.github.gimme.gimmebot.boot.command.executor

import com.github.gimme.gimmebot.core.command.parameter.ParameterType
import kotlin.reflect.KType

/**
 * Thrown when attempting to use a type that is not registered as a valid [ParameterType].
 *
 * @see ParameterTypes.register
 */
class UnsupportedParameterTypeException(type: KType) : RuntimeException(
    "Unsupported parameter type: \"${type}\"." +
            " Custom types can be registered manually with the help of ${ParameterTypes::class.qualifiedName}."
)
