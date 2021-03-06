/*
 * Copyright 2020 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.dcp.highlevel;

import com.couchbase.client.dcp.Client;
import com.couchbase.client.dcp.message.StreamEndReason;

public interface DatabaseChangeListener {

  /**
   * Called when a Couchbase document is created or updated.
   * <p>
   * <b>NOTE:</b> If flow control is enabled on the client, listeners registered via
   * {@link Client#nonBlockingListener(DatabaseChangeListener)} and listeners using
   * {@link FlowControlMode#MANUAL} <b>MUST</b> call {@link Mutation#flowControlAck()}
   * when the application has finished processing the event, otherwise the server will
   * stop sending events.
   */
  default void onMutation(Mutation mutation) {
    mutation.flowControlAck();
  }

  /**
   * Called when a Couchbase document is deleted or expired.
   * <p>
   * <b>NOTE:</b> If flow control is enabled on the client, listeners registered via
   * {@link Client#nonBlockingListener(DatabaseChangeListener)} and listeners using
   * {@link FlowControlMode#MANUAL} <b>MUST</b> call {@link Deletion#flowControlAck()}
   * when the application has finished processing the event, otherwise the server will
   * stop sending events.
   */
  default void onDeletion(Deletion deletion) {
    deletion.flowControlAck();
  }

  /**
   * Called when a vbucket sequence number has advanced due to an event other than
   * a document change, or for events the consumer is not subscribed to.
   * <p>
   * Collections-aware listeners SHOULD use the offset from this notification
   * to update their stream state just as if they had received a document change
   * at this offset. Otherwise they risk "rollback to zero" when the stream is
   * restarted from an offset prior to the purge seqno.
   * <p>
   * Only sent if the client is collections-aware.
   */
  default void onSeqnoAdvanced(SeqnoAdvanced seqnoAdvanced) {
  }

  /**
   * Called when a new scope is created.
   * <p>
   * The listener will only receive this notification if all of the following
   * conditions are met:
   * <ul>
   * <li>The client is collections-aware.
   * <li>The client is not configured with a scope or collections filter.
   * <li>The user has permission to view the new scope.
   * </ul>
   */
  default void onScopeCreated(ScopeCreated scopeCreated) {
  }

  /**
   * Called when a scope is dropped.
   * <p>
   * The listener will only receive this notification if all of the following
   * conditions are met:
   * <ul>
   * <li>The client is collections-aware.
   * <li>The client is not configured with a scope or collections filter,
   * OR the scope filter specifies the dropped scope.
   * <li>The user had permission to view the dropped scope.
   * </ul>
   */
  default void onScopeDropped(ScopeDropped scopeDropped) {
  }

  /**
   * Called when a collection is created.
   * <p>
   * The listener will only receive this notification if all of the following
   * conditions are met:
   * <ul>
   * <li>The client is collections-aware.
   * <li>The client is not configured with a scope or collections filter,
   * OR the scope filter matches the new collection's parent scope,
   * OR the collections filter includes the collection.
   * <li>The user has permission to view the new collection.
   * </ul>
   */
  default void onCollectionCreated(CollectionCreated collectionCreated) {
  }

  /**
   * Called when a collection is dropped.
   * <p>
   * The listener will only receive this notification if all of the following
   * conditions are met:
   * <ul>
   * <li>The client is collections-aware.
   * <li>The client is not configured with a scope or collections filter,
   * OR the scope filter matches the new collection's parent scope,
   * OR the collections filter includes the collection.
   * <li>The user had permission to view the dropped collection.
   * </ul>
   */
  default void onCollectionDropped(CollectionDropped collectionDropped) {
  }

  /**
   * Called when a collection is flushed.
   * <p>
   * The listener will only receive this notification if all of the following
   * conditions are met:
   * <ul>
   * <li>The client is collections-aware.
   * <li>The client is not configured with a scope or collections filter,
   * OR the scope filter matches the new collection's parent scope,
   * OR the collections filter includes the collection.
   * <li>The user has permission to view the flushed collection.
   * </ul>
   */
  default void onCollectionFlushed(CollectionFlushed collectionFlushed) {
  }

  default void onRollback(Rollback rollback) {
    // Most clients just want to resume streaming.
    rollback.resume();
  }

  default void onSnapshot(SnapshotDetails snapshotDetails) {
    // Most clients won't care, since snapshot markers are available
    // in the "offset" of mutations and deletions.
  }

  default void onFailoverLog(FailoverLog failoverLog) {
    // Most clients won't care.
  }

  /**
   * <b>NOTE:</b> The DCP client will automatically attempt to reopen the stream
   * if the reason is not {@link StreamEndReason#OK}.
   */
  default void onStreamEnd(StreamEnd streamEnd) {
    // Most clients won't care, since the client auto-reopens aborted streams.
  }

  /**
   * Something bad and probably unrecoverable happened.
   */
  void onFailure(StreamFailure streamFailure);

}
