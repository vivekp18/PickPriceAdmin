package com.efunhub.pickpriceadmin.Interface;

import com.android.volley.VolleyError;

/**
 * Created by Admin on 06-12-2018.
 */

public interface IResult {


    void notifySuccess(int requestId, String response);
    void notifyError(int requestId, VolleyError error);
}
