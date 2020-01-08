package org.haobtc.wallet.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.wallet.R;
import org.haobtc.wallet.bean.GetnewcreatTrsactionListBean;

import java.util.List;

public class MoreAddressAdapter extends BaseQuickAdapter<GetnewcreatTrsactionListBean.OutputAddrBean, BaseViewHolder> {

    public MoreAddressAdapter(@Nullable List<GetnewcreatTrsactionListBean.OutputAddrBean> data) {
        super(R.layout.moreaddress_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetnewcreatTrsactionListBean.OutputAddrBean item) {
        helper.setText(R.id.tet_moreaddress,item.getAddr()).setText(R.id.tet_payNum,item.getAmount());

    }
}