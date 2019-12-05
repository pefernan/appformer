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

package org.uberfire.collaboration.session.impl;

import org.jboss.errai.security.shared.api.identity.User;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.collaboration.service.event.NewCommandEvent;
import org.uberfire.collaboration.session.CollaborationCommandExecutor;
import org.uberfire.collaboration.session.CollaborationCommandInfo;
import org.uberfire.collaboration.session.CollaborationSession;
import org.uberfire.collaboration.store.CollaborationCommand;
import org.uberfire.collaboration.store.CollaborationCommandFactory;
import org.uberfire.collaboration.store.InitializationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CollaborationSessionImpl implements CollaborationSession {

    private String id;
    private User user;

    private CollaborationCommandFactory commandFactory;
    private CollaborationCommandExecutor executor;
    private Consumer<CollaborationCommandInfo> infoConsumer;

    private List<CollaborationCommand> commands = new ArrayList<>();

    public CollaborationSessionImpl(String uuid,
                                    User user,
                                    CollaborationCommandFactory commandFactory,
                                    CollaborationCommandExecutor executor,
                                    Consumer<CollaborationCommandInfo> infoConsumer) {
        this.id = uuid;
        this.user = user;
        this.commandFactory = commandFactory;
        this.executor = executor;
        this.infoConsumer = infoConsumer;
    }

    @Override
    public CollaborationCommandInfo execute(InitializationContext context) {
        return registerCommandContext(context, true);
    }

    @Override
    public void notifyNewCommandEvent(NewCommandEvent event) {
        CollaborationCommandInfo commandInfo = event.getCommandInfo();

        if (!commandInfo.getSessionId().equals(id) && !commandInfo.getUserId().equals(user.getIdentifier())) {
            registerCommandContext(event.getCommandInfo().getContext(), false);
        }
    }

    private CollaborationCommandInfo registerCommandContext(InitializationContext context, boolean notify) {

        PortablePreconditions.checkNotNull("context", context);

        CollaborationCommand command = commandFactory.newCommand(context);

        PortablePreconditions.checkNotNull("command", command);

        CollaborationCommandInfo info  = new CollaborationCommandInfo(id, user.getIdentifier(), context);

        commands.add(command);

        if(notify) {
            infoConsumer.accept(info);
        }

        executor.execute(command);

        return info;
    }
}
