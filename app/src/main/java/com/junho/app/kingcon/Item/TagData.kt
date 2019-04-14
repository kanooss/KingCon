package com.junho.app.kingcon.Item

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIgnore
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import java.io.Serializable

@DynamoDBTable(tableName = "AppData")
data class TagData(@get:DynamoDBHashKey(attributeName = "Partition") var id: String = "TAG",
                   @get:DynamoDBAttribute(attributeName = "TagTitle")var title: ArrayList<String>,
                   @get:DynamoDBAttribute(attributeName = "Tag")var tag: HashMap<String, ArrayList<String>>,
                   @get:DynamoDBIgnore var tagColor: HashMap<String ,ArrayList<IntArray>>) : Serializable {
    constructor():this("TAG", arrayListOf(), hashMapOf(), hashMapOf())
    constructor(title: ArrayList<String>, tag: HashMap<String, ArrayList<String>>):this("TAG", title, tag, hashMapOf())
}
@DynamoDBTable(tableName = "Version")
data class TagVersion(@get:DynamoDBHashKey(attributeName = "Partition") var partition: String = "TAG",
                      @get:DynamoDBAttribute(attributeName = "Version") var version: Int?): Serializable{
    constructor():this("TAG", 0)
}