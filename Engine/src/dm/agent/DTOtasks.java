package dm.agent;

import dto.DTOstatus;
import problem.Problem;

import java.util.List;

public class DTOtasks extends DTOstatus {

    private List<AgentTask> taskList;

    public DTOtasks(boolean isSucceed, Problem details, List<AgentTask> taskList) {
        super(isSucceed, details);
        this.taskList = taskList;
    }

    public List<AgentTask> getTaskList() {
        return taskList;
    }
}
