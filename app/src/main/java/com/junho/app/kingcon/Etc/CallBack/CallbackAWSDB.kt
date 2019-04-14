package com.junho.app.kingcon.Etc.CallBack

import com.junho.app.kingcon.Item.TagData

interface CallbackAWSDB {
    interface Login{
        fun isUserSignUp(id: Boolean, info: Boolean   )
    }
    interface Tag{
        fun callbackTagData(data: TagData)
    }
}