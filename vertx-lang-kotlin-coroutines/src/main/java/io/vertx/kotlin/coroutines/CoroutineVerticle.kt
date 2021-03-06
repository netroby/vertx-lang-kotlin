package io.vertx.kotlin.coroutines

import io.vertx.core.Context
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import kotlinx.coroutines.experimental.launch

/**
 * A Verticle which run its start and stop methods in coroutine.
 * You should subclass this class instead of AbstractVerticle to create any verticles that use vertx-kotlin-coroutine.
 *
 * @author <a href="http://www.streamis.me">Stream Liu</a>
 */
abstract class CoroutineVerticle : Verticle {

  private lateinit var vertxInstance: Vertx
  protected lateinit var context: Context

  override fun init(vertx: Vertx, context: Context) {
    this.vertxInstance = vertx
    this.context = context
  }

  override fun getVertx(): Vertx = vertxInstance

  override fun start(startFuture: Future<Void>?) {
    launch(context.dispatcher()) {
      try {
        start()
        startFuture?.complete()
      } catch (t: Throwable) {
        startFuture?.fail(t)
      }
    }
  }

  override fun stop(stopFuture: Future<Void>?) {
    launch(context.dispatcher()) {
      try {
        stop()
        stopFuture?.complete()
      } catch (t: Throwable) {
        stopFuture?.fail(t)
      }
    }
  }

  /**
   * Get the deployment ID of the verticle deployment
   * @return the deployment ID
   */
  protected val deploymentID: String by lazy {
    context.deploymentID()
  }

  /**
   * Get the configuration of the verticle.
   *
   *
   * This can be specified when the verticle is deployed.
   * @return the configuration
   */
  protected val config : JsonObject by lazy {
    context.config()
  }

  /**
   * Get the arguments used when deploying the Vert.x process.
   * @return the list of arguments
   */
  protected val processArgs : List<String> by lazy {
    context.processArgs()
  }

  /**
   * Override this method in your verticle
   */
  protected open suspend fun start() {}

  /**
   * Override this method in your verticle
   */
  protected open suspend fun stop() {}
}
