package com.crediya.requests.model.solicitude.gateways;

import com.crediya.requests.model.solicitude.Solicitude;
import reactor.core.publisher.Mono;

public interface SolicitudeStatusNotifier {
    Mono<Void> notifyStatusChanged(Solicitude solicitude);
}
