package org.xpathqs.driver.widgets

import org.xpathqs.driver.model.IBaseModel

interface IFormInput {
    fun input(text: String, model: IBaseModel? = null)
    fun clear(model: IBaseModel? = null) = input("", model)
    fun focus() {}

    fun isDisabled(): Boolean {
        return false
    }
    fun isValidationError(): Boolean {
        return false
    }
}