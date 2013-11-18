package se.slashat.slashapp.fragments.highfive;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;

import se.slashat.slashapp.Callback;
import se.slashat.slashapp.R;
import se.slashat.slashapp.adapter.MyHighFiversArrayAdapter;
import se.slashat.slashapp.model.SectionModel;
import se.slashat.slashapp.model.highfive.HighFiver;
import se.slashat.slashapp.model.highfive.User;
import se.slashat.slashapp.service.HighFiveService;
import se.slashat.slashapp.viewmodel.HighFiverViewModel;
import se.slashat.slashapp.viewmodel.SectionViewModel;
import se.slashat.slashapp.viewmodel.ViewModelBase;

/**
 * Created by nicklas on 9/28/13.
 */
public class MyHighFiversFragment extends ListFragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.myhighfivers_fragment, null);

        //populate(true);
        return view;
    }

    private void populate(boolean reload) {
        HighFiveService.getUser(
                new Callback<User>() {
                    @Override
                    public void call(User user) {
                        if (user == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                            AlertDialog alertDialog = builder.setMessage("Kunde inte ta emot data från servern. Detta kan bero på dålig nätanslutning men ifall du loggat in med samma konto på en annan enhet måste du för tillfället ominstallera denna app för att logga in igen")
                                    .setCancelable(true).create();
                            alertDialog.show();
                        } else {
                            Collection<HighFiver> highFivers = user.getHighFivers();
                            if (!highFivers.isEmpty()) {
                                ArrayList<ViewModelBase> list = new ArrayList<ViewModelBase>();

                                list.add(new SectionViewModel(new SectionModel("Mina High-Fivers:")));

                                for (HighFiver highFiver : highFivers) {
                                    HighFiverViewModel highFiverViewModel = new HighFiverViewModel(highFiver);
                                    list.add(highFiverViewModel);
                                }

                                list.add(new SectionViewModel(new SectionModel("")));

                                MyHighFiversArrayAdapter myHighFiversArrayAdapter = new MyHighFiversArrayAdapter(getActivity(), R.layout.about_list_item_row, list.toArray(new ViewModelBase[list.size()]));
                                setListAdapter(myHighFiversArrayAdapter);
                            }
                        }
                    }
                }, reload);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (HighFiveService.hasToken()) {
                populate(true);
            }
        } catch (Exception e) {
            //supress errors
            e.printStackTrace();
        }
    }
}