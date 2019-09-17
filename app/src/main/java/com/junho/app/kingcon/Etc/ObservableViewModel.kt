/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.junho.app.kingcon.Etc

import androidx.lifecycle.ViewModel
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry

/**
 * An [Observable] [ViewModel] for Data Binding.
 */
open class ObservableViewModel : ViewModel(), Observable {

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) = callbacks.add(callback)

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) = callbacks.remove(callback)

    /**
     * 이 인스턴스의 모든 속성이 변경된 것을 리스너에 알립니다.
     */
    @Suppress("unused")
    fun notifyChange() = callbacks.notifyCallbacks(this, 0, null)

    /**
     * 특정의 프롭퍼티가 변경된 것을 청취자(Notifies listeners)에게 통지합니다.
* 변경되는 속성에 대한 getter는 [Bindable]로 표시되어
* 'BR'에 필드를 생성하여 'fieldId'로 사용해야합니다.
*
* 변경을 뷰에 알려 뷰도 데이터를 변경 할 수 있도록
*
* @param fieldId 생성 된 Bindable 필드의 BR id입니다.
*/
fun notifyPropertyChanged(fieldId: Int) = callbacks.notifyCallbacks(this, fieldId, null)
}
