package io.pivotal.pal.tracker.timesheets;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectClient {

    private final RestOperations restOperations;
    private Map<Long, ProjectInfo> cachedProjects = new ConcurrentHashMap<>();
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo fetchedProjectInfo = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        cachedProjects.put(projectId, fetchedProjectInfo);
        return fetchedProjectInfo;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        return cachedProjects.get(projectId);
    }
}
