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

package org.uberfire.collaboration.service.impl;

import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.collaboration.service.CollaborationSessionService;
import org.uberfire.collaboration.session.CollaborationCommandInfo;
import org.uberfire.collaboration.service.event.NewCommandEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

@Service
@ApplicationScoped
public class CollaborationSessionServiceImpl implements CollaborationSessionService {

    private Event<NewCommandEvent> commandEvent;

    @Inject
    public CollaborationSessionServiceImpl(Event<NewCommandEvent> commandEvent) {
        this.commandEvent = commandEvent;
    }

    @Override
    public void notifyNewCommand(CollaborationCommandInfo commandInfo) {
        if (commandInfo != null) {
            commandEvent.fire(new NewCommandEvent(commandInfo));
        }
    }
}
