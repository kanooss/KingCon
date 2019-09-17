package com.junho.app.kingcon.Adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.junho.app.kingcon.Etc.CallBack.CallbackAdapter
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R
import kotlinx.android.synthetic.main.adapter_home_list.view.*

class ProductListAdapter(
    val context: Context,
    private val productData: ArrayList<ProductData>,
    private val callback: CallbackAdapter.Product
) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {
    override fun getItemCount(): Int = productData.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.adapter_home_list, parent, false)
        return ViewHolder(mainView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(context, productData[position], position, callback)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun onBind(
            context: Context,
            productData: ProductData,
            position: Int,
            callback: CallbackAdapter.Product
        ) {
            //사진
            Util.getPostPicture(context, productData.id, view.ivProduct)
            //제조사
            view.tvProductCompany.text = productData.company
            //이름
            view.tvProductName.text = productData.name
            //평가자 수
            view.tvRatingNum.text = productData.rating.toString()
            //위시 등록 수
            view.tvWishNum.text = productData.wish.toString()
            //리뷰 수
            view.tvReviewNum.text = productData.review.toString()
            //평점
            val point = String.format("%.2f", productData.point)
            view.tvProductPoint.text = point
            //태그 리사이클러뷰
            val tagListAdapter = ProductTagListAdapter(context, productData.tag)
            view.rvTagList.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            view.rvTagList.adapter = tagListAdapter
            //제품 클릭 콜백
            view.productContainer.setOnClickListener { callback.click(productData) }
        }
    }

}