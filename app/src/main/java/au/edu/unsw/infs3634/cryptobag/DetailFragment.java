package au.edu.unsw.infs3634.cryptobag;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import au.edu.unsw.infs3634.cryptobag.Entities.Coin;
import au.edu.unsw.infs3634.cryptobag.Entities.CoinLoreResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    private Coin mCoin;

    public DetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetCoinTask().execute();

     /*   if(getArguments().containsKey(ARG_ITEM_ID)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coinlore.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            CoinService service = retrofit.create(CoinService.class);
            Call<CoinLoreResponse> coinsCall = service.getCoins();
            coinsCall.enqueue(new Callback<CoinLoreResponse>() {
                @Override
                public void onResponse(Call<CoinLoreResponse> call, Response<CoinLoreResponse> response) {
                    List<Coin> coins = response.body().getData();
                    for(Coin coin : coins) {
                        if(coin.getId().equals(getArguments().getString(ARG_ITEM_ID))) {
                            mCoin = coin;
                            break;
                        }
                    }
                    updateUi();
                    DetailFragment.this.getActivity().setTitle(mCoin.getName());
                }

                @Override
                public void onFailure(Call<CoinLoreResponse> call, Throwable t) {

                }
            });
        } */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
       // updateUi();
        return rootView;
    }

    private class GetCoinTask extends AsyncTask<Void, Void, List<Coin>> {
        @Override
        protected List<Coin> doInBackground(Void... voids) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://api.coinlore.com")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            CoinService service = retrofit.create(CoinService.class);
            Call<CoinLoreResponse> coinsCall = service.getCoins();
            Response<CoinLoreResponse> coinResponse = null;
            try {
                coinResponse = coinsCall.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Coin> coins = coinResponse.body().getData();


            return coins;

        }

        @Override
        protected void onPostExecute(List<Coin> coins) {
             for(Coin coin:coins) {
                 if(coin.getId().equals(getArguments().getString(ARG_ITEM_ID))) {
                     mCoin =coin;
                     updateUi();
                     break;
                 }
             }
            DetailFragment.this.getActivity().setTitle(mCoin.getName());
        }
    }


    private void updateUi() {
        View rootView = getView();
        if(mCoin != null) {
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            ((TextView) rootView.findViewById(R.id.tvName)).setText(mCoin.getName());
            ((TextView) rootView.findViewById(R.id.tvSymbol)).setText(mCoin.getSymbol());
            ((TextView) rootView.findViewById(R.id.tvValueField)).setText(formatter.format(Double.valueOf(mCoin.getPriceUsd())));
            ((TextView) rootView.findViewById(R.id.tvChange1hField)).setText(mCoin.getPercentChange1h() + " %");
            ((TextView) rootView.findViewById(R.id.tvChange24hField)).setText(mCoin.getPercentChange24h() + " %");
            ((TextView) rootView.findViewById(R.id.tvChange7dField)).setText(mCoin.getPercentChange7d() + " %");
            ((TextView) rootView.findViewById(R.id.tvMarketcapField)).setText(formatter.format(Double.valueOf(mCoin.getMarketCapUsd())));
            ((TextView) rootView.findViewById(R.id.tvVolumeField)).setText(formatter.format(Double.valueOf(mCoin.getVolume24())));
            ((ImageView) rootView.findViewById(R.id.ivSearch)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchCoin(mCoin.getName());
                }
            });
        }
    }

    private void searchCoin(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + name));
        startActivity(intent);
    }
}
