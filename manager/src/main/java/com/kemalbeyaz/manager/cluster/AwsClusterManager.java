package com.kemalbeyaz.manager.cluster;

import com.kemalbeyaz.manager.redis.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AwsClusterManager {

    private static final Logger LOG = LoggerFactory.getLogger(AwsClusterManager.class);
    private static final String CLUSTER_NAME = "redis-demo-super-cluster";
    private static final String TASK_NAME = "redis-demo-super-task";
    private static final List<String> SUBNET_IDS =
            new ArrayList<>(Arrays.asList("subnet-03341a4e36d1eac32", "subnet-0a034690926e2b852", "subnet-00706ac17c0a1971d"));

    public AwsClusterManager() {
        LOG.info("AWS Cluster Manager initialized.");
    }

    public void createService(String serviceName, int desireCount) {

        var awsVpcConfiguration = createAwsVpcConfiguration();
        var networkConfiguration = createNetworkConfiguration(awsVpcConfiguration);
        var createServiceRequest = createCreateServiceRequest(networkConfiguration, serviceName, desireCount);

        try (var ecsClient = getEcsClient()) {
            var createServiceResponse = ecsClient.createService(createServiceRequest);
            LOG.info("ECS services created!");

            List<Deployment> deployments = createServiceResponse.service().deployments();
            System.out.println(deployments);
        }
    }

    public void deleteService(String serviceName) {

        var deleteServiceRequest = createDeleteServiceRequest(serviceName);

        try (var ecsClient = getEcsClient()) {
            var deleteServiceResponse = ecsClient.deleteService(deleteServiceRequest);
            LOG.info("ECS services deleted!");
            System.out.println(deleteServiceResponse);
        }
    }

    private static EcsClient getEcsClient() {
        return EcsClient.builder()
                .region(Region.US_WEST_2)
                .build();
    }

    private AwsVpcConfiguration createAwsVpcConfiguration() {
        return AwsVpcConfiguration.builder()
                .assignPublicIp(AssignPublicIp.ENABLED)
                .subnets(SUBNET_IDS)
                .build();
    }

    private NetworkConfiguration createNetworkConfiguration(AwsVpcConfiguration awsVpcConfiguration) {
        return NetworkConfiguration.builder()
                .awsvpcConfiguration(awsVpcConfiguration)
                .build();
    }

    private CreateServiceRequest createCreateServiceRequest(NetworkConfiguration networkConfiguration, String serviceName, int desireCount) {
        return CreateServiceRequest.builder()
                .cluster(CLUSTER_NAME)
                .taskDefinition(TASK_NAME)
                .launchType(LaunchType.FARGATE)
                .networkConfiguration(networkConfiguration)
                .desiredCount(desireCount)
                .serviceName(serviceName)
                .build();
    }

    private DeleteServiceRequest createDeleteServiceRequest(String serviceName) {
        return DeleteServiceRequest.builder()
                .cluster(CLUSTER_NAME)
                .service(serviceName)
                .force(true)
                .build();
    }
}
