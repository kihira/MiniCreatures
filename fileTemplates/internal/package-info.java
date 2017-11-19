#parse("File Header.java")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import mcp.MethodsReturnNonnullByDefault;
import javax.annotation.ParametersAreNonnullByDefault;