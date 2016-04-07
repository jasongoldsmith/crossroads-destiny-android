package com.example.sharmha.travelerfordestiny.views;

import android.content.Context;
import android.widget.RelativeLayout;

import com.example.sharmha.travelerfordestiny.data.PlayerData;

import java.util.ArrayList;

/**
 * Created by sharmha on 3/1/16.
 */
public class PlayerProfileImageView extends RelativeLayout {

    private ArrayList<PlayerData> _players;

    public PlayerProfileImageView(Context context) {
        super(context);

        this._players = new ArrayList<PlayerData>();
    }



}
