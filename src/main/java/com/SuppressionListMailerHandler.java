package com;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.function.aws.MicronautRequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Introspected
public class SuppressionListMailerHandler extends MicronautRequestHandler<Void, Void> {

  private static final Logger LOG = LogManager.getLogger(SuppressionListMailerHandler.class);

  @Override
  public Void execute(Void input) {
    LOG.info("Requesting suppression list ...");

    return null;
  }
}
