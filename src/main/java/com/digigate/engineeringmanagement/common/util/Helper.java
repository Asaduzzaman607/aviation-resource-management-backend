package com.digigate.engineeringmanagement.common.util;

import com.digigate.engineeringmanagement.common.authentication.security.services.UserDetailsImpl;
import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.digigate.engineeringmanagement.common.constant.ApprovalStatusType;
import com.digigate.engineeringmanagement.common.constant.ErrorId;
import com.digigate.engineeringmanagement.common.exception.EngineeringManagementServerException;
import com.digigate.engineeringmanagement.common.loader.SubModuleJsonLoader;
import com.digigate.engineeringmanagement.common.payload.response.PageData;
import com.digigate.engineeringmanagement.storemanagement.constant.RemarkType;
import com.digigate.engineeringmanagement.storemanagement.payload.request.storedemand.StoreDemandDetailsDto;
import com.digigate.engineeringmanagement.storemanagement.service.storeconfiguration.PartRemarkService;
import com.digigate.engineeringmanagement.storemanagement.service.storedemand.ApprovalStatusService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.*;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
public class Helper {
    private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);

    private final HttpServletRequest request;
    private final ApprovalStatusService approvalStatusService;
    private final PartRemarkService partRemarkService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    public Helper(HttpServletRequest request,
                  @Lazy ApprovalStatusService approvalStatusService,
                  @Lazy PartRemarkService partRemarkService) {
        this.request = request;
        this.approvalStatusService = approvalStatusService;
        this.partRemarkService = partRemarkService;
    }

    public static Long getAuthUserId() {
        Long userId = ApplicationConstant.ANONYMOUS_USER;
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        } catch (Exception e) {
            LOGGER.error("Auth data could not be extracted: {}", e.getMessage());
        }
        return userId;
    }

    public static String convertSnakeToHrf(String unreadableString) {
        return Arrays.stream(unreadableString.split(UNDER_SCORE)).map(strItem ->
                String.valueOf(strItem.charAt(0)).toUpperCase().concat(strItem.substring(1).toLowerCase()))
                .collect(Collectors.joining(SPACE));
    }

    /**
     * This method will prepare Preview Model  for PageData
     * @param data of Collection {@link  Collections}
     * @param pageable          {@link   Pageable}
     * @return                  {@link StoreDemandDetailsDto  }
     */

    public static <T> PageData buildCustomPagedData(List<T> data, Pageable pageable) {
        List<T> model = new ArrayList<>();
        int start = VALUE_ZERO;
        if (!Objects.equals(pageable.getPageNumber(), FIRST_INDEX)) {
            start = (pageable.getPageNumber() * pageable.getPageSize());
        }
        for (int i = start; i < start + pageable.getPageSize(); i++) {
            if (i < data.size()) {
                model.add(data.get(i));
            } else {
                break;
            }
        }
        return PageData.builder()
                .model(model)
                .totalElements(data.size())
                .currentPage(pageable.getPageNumber() + INT_ONE)
                .totalPages((data.size() / pageable.getPageSize()) + INT_ONE)
                .build();
    }

    public static <T> Page buildCustomPage(List<T> data, Pageable pageable) {
        int startIndex = (int) pageable.getOffset();
        int endIndex = (int) ((pageable.getOffset() + pageable.getPageSize()) > data.size() ? data.size() : pageable.getOffset() + pageable.getPageSize());
        List subList = data.subList(startIndex, endIndex);
        return new PageImpl(subList, pageable, data.size());
    }

    public static String addBreakLine(String name, String key) {
        return Objects.nonNull(name) ? key + name + "\n" : " " + "\n";
    }

    public String getRequestUri() {
        return this.request.getRequestURI().substring(contextPath.length());
    }

    public Long getSubModuleItemId() {

        Long subModuleId = getSubModuleItemIdOrZero();

        if (subModuleId == 0L) {
            LOGGER.error("Sub-module item id id could not be extracted");
            throw EngineeringManagementServerException.badRequest(ErrorId.SUBMODULE_ITEM_ID_REQUIRED);
        }

        return subModuleId;
    }

    public Long getSubModuleItemIdOrZero() {
        String path = getRequestUri();
        PathMatcher pathMatcher = new AntPathMatcher();

        Long subModuleId = 0L;
        char[] charPath = path.toCharArray();
        StringBuilder pathBuilder = new StringBuilder(path);

        if (charPath[charPath.length - 1] == ApplicationConstant.SLASH_CHAR) {
            pathBuilder.deleteCharAt(path.length() - 1);
        }

        for (Map.Entry<String, Long> api: SubModuleJsonLoader.SUB_MODULE_APIS.entrySet()) {
            if (pathMatcher.match(api.getKey(), pathBuilder.toString())) {
                subModuleId = (Long) api.getValue();
                break;
            }
        }
        return subModuleId;
    }

    public static String createDynamicCode(String errorCode, List<String> placeHolders) {
        StringBuilder builder = new StringBuilder(errorCode);
        if (CollectionUtils.isEmpty(placeHolders)) {
            return errorCode;
        }
        placeHolders.forEach(placeHolder -> builder.append(MESSAGE_SEPARATOR).append(placeHolder));
        return builder.toString();
    }
    public static String createDynamicCode(String errorCode, String... placeHolders) {
        StringBuilder builder = new StringBuilder(errorCode);
        if (Objects.isNull(placeHolders)) {
            return errorCode;
        }
        Arrays.stream(placeHolders).forEach(placeHolder -> builder.append(MESSAGE_SEPARATOR).append(placeHolder));
        return builder.toString();
    }
    public String parseJwt() {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public static Integer toInt(Double value) {
        if (value == null) return 0;
        else return value.intValue();
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public void deleteAllByParentIdAndApprovalStatusTypes(long parentId, List<ApprovalStatusType> statusTypes) {
        approvalStatusService.deleteAllByParentIdAndApprovalStatusType(parentId, statusTypes);
    }

    public void deleteByParentIdAndRemarkTypeIn(long parentId, List<RemarkType> remarkTypes) {
        partRemarkService.deleteByParentIdAndRemarkTypeIn(parentId, remarkTypes);
    }
}
