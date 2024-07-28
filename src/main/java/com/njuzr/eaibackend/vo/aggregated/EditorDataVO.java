package com.njuzr.eaibackend.vo.aggregated;

import com.njuzr.eaibackend.vo.AIDialogueVO;
import com.njuzr.eaibackend.vo.AssignmentVO;
import com.njuzr.eaibackend.vo.EngagementVO;
import lombok.Data;

/**
 * @author: Leonezhurui
 * @Date: 2024/3/19 - 20:07
 * @Package: EAI-Backend
 */

@Data
public class EditorDataVO {
    private AssignmentVO assignmentVO;
    private EngagementVO engagementVO;
    private AIDialogueVO aiDialogueVO;
}
