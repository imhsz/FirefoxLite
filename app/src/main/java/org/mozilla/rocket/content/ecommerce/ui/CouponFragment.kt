package org.mozilla.rocket.content.ecommerce.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import dagger.Lazy
import kotlinx.android.synthetic.main.fragment_coupon.*
import org.mozilla.focus.R
import org.mozilla.rocket.adapter.AdapterDelegatesManager
import org.mozilla.rocket.adapter.DelegateAdapter
import org.mozilla.rocket.content.appComponent
import org.mozilla.rocket.content.common.ui.ContentTabActivity
import org.mozilla.rocket.content.ecommerce.ui.adapter.Coupon
import org.mozilla.rocket.content.ecommerce.ui.adapter.CouponAdapterDelegate
import org.mozilla.rocket.content.getActivityViewModel
import javax.inject.Inject

class CouponFragment : Fragment() {

    @Inject
    lateinit var couponViewModelCreator: Lazy<CouponViewModel>

    private lateinit var couponViewModel: CouponViewModel
    private lateinit var couponAdapter: DelegateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent().inject(this)
        super.onCreate(savedInstanceState)
        couponViewModel = getActivityViewModel(couponViewModelCreator)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCoupons()
        bindListData()
        bindPageState()
        observeAction()
    }

    private fun initCoupons() {
        couponAdapter = DelegateAdapter(
            AdapterDelegatesManager().apply {
                add(Coupon::class, R.layout.item_coupon, CouponAdapterDelegate(couponViewModel))
            }
        )
        coupon_list.adapter = couponAdapter
    }

    private fun bindListData() {
        couponViewModel.couponItems.observe(this@CouponFragment, Observer {
            couponAdapter.setData(it)
        })
    }

    private fun bindPageState() {
        couponViewModel.isDataLoading.observe(this@CouponFragment, Observer { state ->
            when (state) {
                is CouponViewModel.State.Idle -> showContentView()
                is CouponViewModel.State.Loading -> showLoadingView()
                is CouponViewModel.State.Error -> showErrorView()
            }
        })
    }

    private fun observeAction() {
        couponViewModel.openCoupon.observe(this, Observer { linkUrl ->
            context?.let {
                startActivity(ContentTabActivity.getStartIntent(it, linkUrl))
            }
        })
    }

    private fun showLoadingView() {
        spinner.visibility = View.VISIBLE
    }

    private fun showContentView() {
        spinner.visibility = View.GONE
    }

    private fun showErrorView() {
        TODO("not implemented")
    }
}