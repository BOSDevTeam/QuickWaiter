package adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosictsolution.quickwaiter.R;

import java.util.List;

import data.BookingData;
import listener.BookingButtonClickListener;

/**
 * Created by NweYiAung on 21-02-2017.
 */
public class BookingListAdapter extends BaseAdapter {

    private Context context;
    BookingButtonClickListener bookingButtonClickListener;
    List<BookingData> lstBookingData;

    public BookingListAdapter(Context context,List<BookingData> lstBookingData){
        this.context=context;
        this.lstBookingData =lstBookingData;
    }

    public void setOnBookingButtonClickListener(BookingButtonClickListener bookingButtonClickListener){
        this.bookingButtonClickListener =bookingButtonClickListener;
    }

    @Override
    public int getCount(){
        return lstBookingData.size();
    }

    @Override
    public String getItem(int position){
        return lstBookingData.get(position).getBookingTableName();
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    static class ViewHolder {
        TextView tvBookingTableName,tvGuestName,tvBookingDate,tvBookingTime,tvPhone,tvPeople;
        ImageButton btnEdit,btnCancel;
    }

    @Override
    public View getView(final int position,View convertView,ViewGroup parent){
        View row;
        final ViewHolder holder;
        if(convertView==null){
            LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=layoutInflater.inflate(R.layout.list_booking, parent,false);
            holder=new ViewHolder();
            holder.tvBookingTableName=(TextView) row.findViewById(R.id.tvBookingTableName);
            holder.tvGuestName=(TextView) row.findViewById(R.id.tvGuestName);
            holder.tvBookingDate=(TextView) row.findViewById(R.id.tvBookingDate);
            holder.tvBookingTime=(TextView) row.findViewById(R.id.tvBookingTime);
            holder.tvPhone=(TextView) row.findViewById(R.id.tvPhone);
            holder.tvPeople=(TextView) row.findViewById(R.id.tvPeople);
            holder.btnEdit=(ImageButton) row.findViewById(R.id.btnEdit);
            holder.btnCancel=(ImageButton) row.findViewById(R.id.btnCancel);

            holder.tvBookingTableName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BOS-PETITE.TTF"));
            holder.tvGuestName.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/ZawgyiOne2008.ttf"));

            row.setTag(holder);
        }
        else{
            row=convertView;
            holder=(ViewHolder) row.getTag();
        }

        holder.tvBookingTableName.setText(lstBookingData.get(position).getBookingTableName());
        holder.tvGuestName.setText(lstBookingData.get(position).getGuestName());
        holder.tvBookingDate.setText(lstBookingData.get(position).getDate());
        holder.tvBookingTime.setText(lstBookingData.get(position).getTime());
        holder.tvPhone.setText(lstBookingData.get(position).getPhone());
        holder.tvPeople.setText(String.valueOf(lstBookingData.get(position).getTotalPeople()));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(bookingButtonClickListener !=null){
                    bookingButtonClickListener.onEditButtonClickListener(position);
                }
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(bookingButtonClickListener !=null){
                    bookingButtonClickListener.onDeleteButtonClickListener(position);
                }
            }
        });

        return row;
    }
}
