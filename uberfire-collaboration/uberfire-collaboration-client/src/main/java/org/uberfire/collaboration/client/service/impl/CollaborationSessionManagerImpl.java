/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.collaboration.client.service.impl;

import org.jboss.errai.common.client.api.Caller;
import org.uberfire.collaboration.client.service.CollaborationSessionManager;
import org.uberfire.collaboration.service.CollaborationSessionService;
import org.uberfire.collaboration.service.event.NewCommandEvent;
import org.uberfire.collaboration.session.CollaborationCommandExecutor;
import org.uberfire.collaboration.session.CollaborationCommandInfo;
import org.uberfire.collaboration.session.CollaborationSession;
import org.uberfire.collaboration.session.impl.CollaborationSessionImpl;
import org.uberfire.collaboration.store.CollaborationCommandFactory;
import org.uberfire.collaboration.store.InitializationContext;
import org.uberfire.rpc.SessionInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class CollaborationSessionManagerImpl implements CollaborationSessionManager {

    private SessionInfo sessionInfo;
    private Caller<CollaborationSessionService> collaborationSessionService;

    private CollaborationSession session;

    @Inject
    public CollaborationSessionManagerImpl(SessionInfo sessionInfo, Caller<CollaborationSessionService> collaborationSessionService) {
        this.sessionInfo = sessionInfo;
        this.collaborationSessionService = collaborationSessionService;
    }

    @Override
    public void init(String uuid, CollaborationCommandFactory store, CollaborationCommandExecutor executor) {
        session = new CollaborationSessionImpl(uuid, sessionInfo.getIdentity(), store, executor, this::notify);
    }

    @Override
    public void newCommand(InitializationContext context) {
        session.execute(context);
    }

    private void notify(CollaborationCommandInfo info) {
        collaborationSessionService.call().notifyNewCommand(info);
    }

    public void onNewEvent(@Observes NewCommandEvent event) {
        session.notifyNewCommandEvent(event);
    }
}
