package com;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.ListSuppressedDestinationsRequest;
import software.amazon.awssdk.services.sesv2.model.ListSuppressedDestinationsResponse;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Singleton
public class SuppressionListMailer {

  private static final String SUPPRESSION_LIST = "Suppression List:";
  private final SesV2Client sesClient;
  List<String> suppressedDestinations;

  private static final String UTF_8 = "UTF-8";

  @Inject
  public SuppressionListMailer(SesV2Client sesClient) {
    this.sesClient = sesClient;
    this.suppressedDestinations = new ArrayList<>();
    getSuppressedDestinations();
  }

  public void sendEmail(String fromAddress, String toAddress) {

    Content subjectContent = Content.builder()
        .data(SUPPRESSION_LIST)
        .charset(UTF_8)
        .build();

    Content textContent = Content.builder()
        .data("Suppression Lists")
        .build();

    Body body = Body.builder()
        .text(textContent)
        .build();

    Message message = Message.builder()
        .subject(subjectContent)
        .body(body)
        .build();

    EmailContent emailContent = EmailContent
        .builder()
        .simple(message);

    SendEmailRequest sendEmailRequest = SendEmailRequest
        .builder()
        .fromEmailAddress(fromAddress)
        .destination(Destination.builder()
            .toAddresses(toAddress)
            .build())
        .content(emailContent)
        .build();

    sesClient.sendEmail(sendEmailRequest);
  }

  private void getSuppressedDestinations() {
    ListSuppressedDestinationsRequest listSuppressedDestinationsRequest = getListSuppressedDestinationsRequest();

    sesClient.listSuppressedDestinationsPaginator(listSuppressedDestinationsRequest)
        .iterator()
        .forEachRemaining(this::addSuppressedDestinationToList);
  }

  private void addSuppressedDestinationToList(ListSuppressedDestinationsResponse listSuppressedDestinationsResponse) {
    Optional<String> suppressedDestinationSummaries = listSuppressedDestinationsResponse.getValueForField(
        "SuppressedDestinationSummaries", String.class);

    suppressedDestinationSummaries.ifPresent(this::addToList);
  }

  private ListSuppressedDestinationsRequest getListSuppressedDestinationsRequest() {
    return ListSuppressedDestinationsRequest.builder()
        .endDate(Instant.now())
        .build();
  }

  private void addToList(String suppressedDestinationSummary) {
    suppressedDestinations.add(suppressedDestinationSummary);
  }
}
