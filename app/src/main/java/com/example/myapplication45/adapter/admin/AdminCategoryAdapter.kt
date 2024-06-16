package com.example.myapplication45.adapter.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication45.databinding.ItemCategoryAdminBinding
import com.example.myapplication45.model.Category
import com.example.myapplication45.util.GlideUtils


class AdminCategoryAdapter(private val mListCategory: List<Category>?,
                           private val iManagerCategoryListener: IManagerCategoryListener
) : RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder>() {
   //chỉ cần gửi sự kiện đến bên ngoài thông qua interface và để các thành phần khác xử lý sự kiện
    interface IManagerCategoryListener {
        fun editCategory(category: Category)
        fun deleteCategory(category: Category)
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemCategoryAdminBinding = ItemCategoryAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(itemCategoryAdminBinding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = mListCategory!![position]
        GlideUtils.loadUrl(category.image, holder.mItemCategoryAdminBinding.imgCategory)
        holder.mItemCategoryAdminBinding.tvCategoryName.text = category.name
        holder.mItemCategoryAdminBinding.imgEdit.setOnClickListener { iManagerCategoryListener.editCategory(category) }
        holder.mItemCategoryAdminBinding.imgDelete.setOnClickListener { iManagerCategoryListener.deleteCategory(category) }
    }

    override fun getItemCount(): Int {
        return mListCategory?.size ?: 0
    }

    class CategoryViewHolder(val mItemCategoryAdminBinding: ItemCategoryAdminBinding) : RecyclerView.ViewHolder(mItemCategoryAdminBinding.root)
}