package com.unp.maps_localizacao;

import java.util.List;

interface DirectionFinderListener {

    void onDirectionFinderStart();

    void onDirectionFinderSuccess(List<Route> routes);

}
