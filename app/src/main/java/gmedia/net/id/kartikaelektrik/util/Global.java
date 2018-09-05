package gmedia.net.id.kartikaelektrik.util;

import java.util.List;

import gmedia.net.id.kartikaelektrik.R;
import gmedia.net.id.kartikaelektrik.model.CustomListItem;

/**
 * Created by Shin on 12/07/2017.
 */

public class Global {

    private ItemValidation iv = new ItemValidation();

    public CustomListItem getStatus(String status){

        CustomListItem item = new CustomListItem();

        switch (iv.parseNullInteger(status)){
            case 1 :
                item = new CustomListItem("Baru", R.color.status_1);
                break;
            case 2 :
                item = new CustomListItem("Pending", R.color.status_2);
                break;
            case 3 :
                item = new CustomListItem("Verified", R.color.status_3);
                break;
            case 4 :
                item = new CustomListItem("Need Accounting Approval", R.color.status_4);
                break;
            case 5 :
                item = new CustomListItem("Need Owner Approval", R.color.status_5);
                break;
            case 7 :
                item = new CustomListItem("Post", R.color.status_7);
                break;
            case 9 :
                item = new CustomListItem("Ditolak", R.color.status_9);
                break;
            default:
                item = new CustomListItem("Baru", R.color.status_1);
                break;
        }

        return item;
    }

    public CustomListItem getStatus(String status, String kiriman){

        CustomListItem item = new CustomListItem();

        if(status.equals("4")){
            item = new CustomListItem("Need Accounting Approval", R.color.status_4);
        }else if(status.equals("3")){

            if(kiriman.equals("0")){
                item = new CustomListItem("Barang Kosong", R.color.status_1);
            }else if(kiriman.equals("1")){
                item = new CustomListItem("Barang Kosong Sebagian", R.color.status_2);
            }else if(kiriman.equals("2")){
                item = new CustomListItem("Terkirim", R.color.status_3);
            }else{
                item = new CustomListItem("Verified", R.color.status_3);
            }
        }else if(status.equals("1")){

            item = new CustomListItem("Baru", R.color.status_1);
        }else if(status.equals("2")){

            item = new CustomListItem("Pending", R.color.status_2);
        }else if(status.equals("5")){
            item = new CustomListItem("Need Owner Approval", R.color.status_5);
        }else if(status.equals("7")){
            item = new CustomListItem("Post", R.color.status_7);
        }else if(status.equals("9")){
            item = new CustomListItem("Ditolak", R.color.status_9);
        }else{
            item = new CustomListItem("Baru", R.color.status_1);
        }


        return item;
    }
}
