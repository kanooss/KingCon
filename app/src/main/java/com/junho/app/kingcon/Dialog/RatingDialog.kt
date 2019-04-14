package com.junho.app.kingcon.Dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.junho.app.kingcon.Etc.CallBack.CallbackDialog
import com.junho.app.kingcon.Etc.Util
import com.junho.app.kingcon.Item.ProductData
import com.junho.app.kingcon.R

class RatingDialog (context: Context, private val product: ProductData, private val callbackDialog: CallbackDialog.Rating) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    override fun getCount(): Int { return 1 }
    override fun getItem(position: Int): Any { return position }
    override fun getItemId(position: Int): Long { return position.toLong() }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        var view: View? = convertView

        if (view == null) {
            view = layoutInflater.inflate(R.layout.dialog_rating, parent, false)
            viewHolder = ViewHolder(view!!.findViewById(R.id.prodCompany), view.findViewById(R.id.prodName), view.findViewById(R.id.prodImage), view.findViewById(R.id.prodRating))
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val context = parent.context
        //상품 데이터 셋
        viewHolder.prodCompany.text = product.company
        viewHolder.prodName.text = product.name
        Util.getPostPicture(context, product.id, viewHolder.prodImage)

        //유저 평가 점수
        viewHolder.prodRatingBar.rating = product.myReview.point
        var tran = true
        viewHolder.prodRatingBar.setOnRatingBarChangeListener { r: RatingBar, point: Float, _: Boolean ->
            if(tran) {
                tran = false
                callbackDialog.onRating(point)
            }
        }

        return view
    }

    data class ViewHolder(val prodCompany: TextView, val prodName: TextView, val prodImage: ImageView, val prodRatingBar: RatingBar)
}