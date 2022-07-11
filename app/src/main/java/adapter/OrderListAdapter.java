package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.TransactionData;
import listener.OrderButtonClickListener;

/**
 * Created by NweYiAung on 14-02-2017.
 */
public class OrderListAdapter extends BaseAdapter {

    private Context context;
    OrderButtonClickListener orderButtonClickListener;
    List<TransactionData> lstTransactionData;
    float floatQty;

    public OrderListAdapter(Context context, List<TransactionData> lstTransactionData) {
        this.context = context;
        this.lstTransactionData = lstTransactionData;
    }

    public void setOnOrderButtonClickListener(OrderButtonClickListener orderButtonClickListener) {
        this.orderButtonClickListener = orderButtonClickListener;
    }

    @Override
    public int getCount() {
        return lstTransactionData.size();
    }

    @Override
    public String getItem(int position) {
        return lstTransactionData.get(position).getItemName();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView tvItemName, tvTaste, tvPNumber, tvTastePrice, tvItemSub, tvTasteMulti;
        EditText etQuantity;
        ImageButton imgbtnPlus, imgbtnMinus, imgbtnCancel, imgbtnCalculator, imgbtnTaste, imgbtnTasteMulti, imgbtnPNumber;
        View row;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_order, parent, false);
            holder = new ViewHolder();
            holder.tvItemName = (TextView) row.findViewById(R.id.tvItemName);
            holder.tvItemSub = (TextView) row.findViewById(R.id.tvItemSub);
            holder.tvTaste = (TextView) row.findViewById(R.id.tvTaste);
            holder.tvTasteMulti = (TextView) row.findViewById(R.id.tvTasteMulti);
            holder.tvPNumber = (TextView) row.findViewById(R.id.tvPNumber);
            holder.tvTastePrice = (TextView) row.findViewById(R.id.tvTastePrice);
            holder.etQuantity = (EditText) row.findViewById(R.id.etQuantity);
            holder.imgbtnPlus = (ImageButton) row.findViewById(R.id.imgbtnPlus);
            holder.imgbtnMinus = (ImageButton) row.findViewById(R.id.imgbtnMinus);
            holder.imgbtnCalculator = (ImageButton) row.findViewById(R.id.imgbtnCalculator);
            holder.imgbtnCancel = (ImageButton) row.findViewById(R.id.imgbtnCancel);
            holder.imgbtnTaste = (ImageButton) row.findViewById(R.id.imgbtnTaste);
            holder.imgbtnTasteMulti = (ImageButton) row.findViewById(R.id.imgbtnTasteMulti);
            holder.imgbtnPNumber = (ImageButton) row.findViewById(R.id.imgbtnPNumber);

            holder.tvItemName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvTaste.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvTasteMulti.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvPNumber.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.etQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));

            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        holder.tvItemName.setText(lstTransactionData.get(position).getItemName());

        if (lstTransactionData.get(position).getAllItemSub().length() != 0) {
            holder.tvItemSub.setVisibility(View.VISIBLE);
            holder.tvItemSub.setText(lstTransactionData.get(position).getAllItemSub());
        } else holder.tvItemSub.setVisibility(View.GONE);

        holder.tvTaste.setText(lstTransactionData.get(position).getTaste());
        holder.tvTasteMulti.setText(lstTransactionData.get(position).getTasteMulti());
        holder.tvTastePrice.setText(String.valueOf(lstTransactionData.get(position).getTastePrice()));
        if (lstTransactionData.get(position).getpNumber() != 0)
            holder.tvPNumber.setText(String.valueOf(lstTransactionData.get(position).getpNumber()));
        floatQty = Float.parseFloat(lstTransactionData.get(position).getStringQty());
        if (floatQty == Math.round(floatQty)) {
            holder.etQuantity.setText(String.valueOf(lstTransactionData.get(position).getIntegerQty()));
        } else {
            holder.etQuantity.setText(String.valueOf(lstTransactionData.get(position).getFloatQty()));
        }

        if (lstTransactionData.get(position).isUseTasteMulti() == 1)
            holder.imgbtnTasteMulti.setVisibility(View.VISIBLE);
        else holder.imgbtnTasteMulti.setVisibility(View.GONE);

        holder.imgbtnPlus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onPlusButtonClickListener(position, holder.etQuantity);
                }
            }
        });

        holder.imgbtnMinus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onMinusButtonClickListener(position, holder.etQuantity);
                }
            }
        });

        holder.imgbtnCalculator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onCalculatorButtonClickListener(position, holder.etQuantity);
                }
            }
        });

        holder.imgbtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onCancelButtonClickListener(position, holder.row);
                }
            }
        });

        holder.imgbtnTaste.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onTasteButtonClickListener(position, holder.tvTaste);
                }
            }
        });

        holder.imgbtnTasteMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onTasteMultiButtonClickListener(position, holder.tvTasteMulti, holder.tvTastePrice);
                }
            }
        });

        holder.imgbtnPNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (orderButtonClickListener != null) {
                    orderButtonClickListener.onPNumberButtonClickListener(position, holder.tvPNumber);
                }
            }
        });

        return row;
    }
}
