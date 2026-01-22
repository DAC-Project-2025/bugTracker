package com.user_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
	@Value("${kafka.topics.user-created}")
	private String userCreatedTopic;

	@Value("${kafka.topics.user-verified}")
	private String userVerifiedTopic;

	@Value("${kafka.topics.user-updated}")
	private String userUpdatedTopic;

	@Value("${kafka.topics.user-deleted}")
	private String userDeletedTopic;

	@Value("${kafka.topics.user-role-assigned}")
	private String userRoleAssignedTopic;

	@Value("${kafka.topics.user-login}")
	private String userLoginTopic;

	@Bean
	public NewTopic userCreatedTopic() {
		return TopicBuilder.name(userCreatedTopic).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic userVerifiedTopic() {
		return TopicBuilder.name(userVerifiedTopic).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic userUpdatedTopic() {
		return TopicBuilder.name(userUpdatedTopic).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic userDeletedTopic() {
		return TopicBuilder.name(userDeletedTopic).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic userRoleAssignedTopic() {
		return TopicBuilder.name(userRoleAssignedTopic).partitions(3).replicas(1).build();
	}

	@Bean
	public NewTopic userLoginTopic() {
		return TopicBuilder.name(userLoginTopic).partitions(3).replicas(1).build();
	}
}
