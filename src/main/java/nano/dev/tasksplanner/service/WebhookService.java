package nano.dev.tasksplanner.service;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nano.dev.tasksplanner.entity.enumeration.Priority;
import nano.dev.tasksplanner.entity.enumeration.Status;
import nano.dev.tasksplanner.entity.enumeration.Type;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WebhookService {

    private final Environment environment;

    private static final String WEBHOOK_URL = "WEBHOOK_URL";
    private static final String LINK_TO_TASK = "http://tasksplanner-env.eba-pkikrhha.us-east-1.elasticbeanstalk.com/dashboard/tasks/";

    public void sendWebhook(String taskName,
                            Type type,
                            Priority status,
                            Status priority,
                            long taskId,
                            List<String> usersDiscordAccount
    )
    {

        log.info("Sending webhook to the discord channel");
        WebhookClientBuilder builder = new WebhookClientBuilder(
                environment.getRequiredProperty(WEBHOOK_URL)
        );
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Hello");
            thread.setDaemon(true);
            return thread;
        });
        builder.setWait(true);
        WebhookClient client = builder.build();

        // send embed
        WebhookEmbed.EmbedField field1 = new WebhookEmbed.EmbedField(true, "title :", taskName);
        WebhookEmbed.EmbedField field2 = new WebhookEmbed.EmbedField(true, "Task type :", String.valueOf(type));
        WebhookEmbed.EmbedField field3 = new WebhookEmbed.EmbedField(true, "Task priority:", String.valueOf(priority));
        WebhookEmbed.EmbedField field4 = new WebhookEmbed.EmbedField(true, "Task status:", String.valueOf(status));

        WebhookEmbed embed = new WebhookEmbedBuilder()
                .setDescription(
                        String.format("Hey %s", usersDiscordAccount.stream()
                        .map(user -> String.format("<@%s>", user))
                        .collect(Collectors.joining(" ")))
                        +" ! You have a new task to do. Check it here : "
                        + LINK_TO_TASK + taskId +"/show"
                )
                .addField(field1)
                .addField(field2)
                .addField(field3)
                .addField(field4)
                .setColor(2817936)
                .build();
        client.send(embed)
                .thenAccept(sentMessage -> log.info("Webhook sent successfully"));
    }
}
