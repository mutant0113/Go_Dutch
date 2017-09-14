package com.mutant.godutch

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import com.mutant.godutch.widget.EmailEditText
import com.mutant.godutch.widget.UserNameEditText
import kotlinx.android.synthetic.main.activity_invite_new_friends.*
import kotlinx.android.synthetic.main.list_item_input_new_friend_info.view.*

class InviteNewFriendsActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_invite_new_friends

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_done -> sendInvitation()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendInvitation() {
        // TODO send invitation

    }

    override fun setup() {
        setupRecycleView()
    }

    private fun setupRecycleView() {
        recyclerView_add_friends.layoutManager = LinearLayoutManager(this@InviteNewFriendsActivity)
        recyclerView_add_friends.adapter = RecycleViewAdapterInviteFriends(this@InviteNewFriendsActivity)
    }

    inner class RecycleViewAdapterInviteFriends(internal var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        inner class FriendInfo {
            var name: String = ""
            var email: String = ""
        }

        private var mNewFriendsInfo: MutableList<FriendInfo> = mutableListOf()
        private val FOOTER_VIEW = 1

        init {
            mNewFriendsInfo.add(FriendInfo())
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == mNewFriendsInfo.size) FOOTER_VIEW else super.getItemViewType(position)
        }

        override fun getItemCount(): Int = if(mNewFriendsInfo.isEmpty()) 1 else mNewFriendsInfo.size + 1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == FOOTER_VIEW) {
                FooterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_friend_invite, parent, false))
            } else {
                ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_input_new_friend_info, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if(holder is ViewHolder) {
                val info = mNewFriendsInfo[position]
                holder.mEditTextName.setText(info.name)
                holder.mEditTextEmail.setText(info.email)

                holder.mEditTextEmail.addTextChangedListener(object: TextWatcher{

                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if(!holder.mEditTextEmail.isValid) holder.mEditTextEmail.error = "error"
                    }
                })
            } else if(holder is FooterViewHolder) {
                holder.itemView.setOnClickListener {
                    Toast.makeText(context, "invite new friends", Toast.LENGTH_LONG).show()
                    mNewFriendsInfo.add(FriendInfo())
                    this.notifyItemInserted(mNewFriendsInfo.size)
                }
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mEditTextName: UserNameEditText = itemView.editText_name
        val mEditTextEmail: EmailEditText = itemView.editText_email
    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

}
