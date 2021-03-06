package org.haobtc.wallet.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import org.haobtc.wallet.R;
import org.haobtc.wallet.event.AddBixinKeyEvent;

import java.util.List;

public class PublicPersonAdapter extends BaseQuickAdapter<AddBixinKeyEvent, BaseViewHolder> {
    public PublicPersonAdapter( @Nullable List<AddBixinKeyEvent> data) {
        super(R.layout.public_person, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddBixinKeyEvent item) {
        helper.setText(R.id.tet_publicperson_item_id,item.getKeyname());
    }
}
