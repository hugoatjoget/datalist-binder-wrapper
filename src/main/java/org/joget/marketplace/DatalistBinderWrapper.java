package org.joget.marketplace;

import bsh.Interpreter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppPluginUtil;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListBinderDefault;
import org.joget.apps.datalist.model.DataListCollection;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.datalist.model.DataListFilterQueryObject;
import org.joget.apps.form.lib.DefaultFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.Form;
import org.joget.apps.form.model.FormBinder;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormDataDeletableBinder;
import org.joget.apps.form.model.FormLoadBinder;
import org.joget.apps.form.model.FormLoadElementBinder;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreElementBinder;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.ApplicationPlugin;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.workflow.model.service.WorkflowManager;
import org.joget.workflow.util.WorkflowUtil;

public class DatalistBinderWrapper extends DataListBinderDefault{
    private static String MESSAGE_PATH = "messages/datalistBinderWrapper";
    
    @Override
    public String getName() {
        return "Datalist Binder Wrapper";
    }

    @Override
    public String getVersion() {
        return "8.0.0";
    }

    @Override
    public String getDescription() {
        return "Datalist Binder Wrapper";
    }

    @Override
    public String getLabel() {
        return "Datalist Binder Wrapper";
    }

    
    @Override
    public String getPropertyOptions() {
        AppDefinition appDef = AppUtil.getCurrentAppDefinition();
        String appId = appDef.getId();
        String appVersion = appDef.getVersion().toString();
        Object[] arguments = new Object[]{getLabel(), appId, appVersion};
        String json = AppUtil.readPluginResource(getClass().getName(), "/properties/datalistBinderWrapper.json", arguments, true, MESSAGE_PATH);
        return json;
    }
    
    protected Object executeScript(String script, Map properties) {
        Object result = null;
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(getClass().getClassLoader());
            for (Object key : properties.keySet()) {
                interpreter.set(key.toString(), properties.get(key));
            }
            LogUtil.debug(getClass().getName(), "Executing script " + script);
            result = interpreter.eval(script);
            return result;
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error executing script");
            return null;
        }
    }
//
//    @Override
//    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
//        FormRowSet result = rows;
//        if (rows != null && !rows.isEmpty()) {
//            // store form data to DB
//            result = super.store(element, rows, formData);
//
//            // handle workflow variables
//            if (!rows.isMultiRow()) {
//                String activityId = formData.getActivityId();
//                String processId = formData.getProcessId();
//                if (activityId != null || processId != null) {
//                    WorkflowManager workflowManager = (WorkflowManager) WorkflowUtil.getApplicationContext().getBean("workflowManager");
//
//                    // recursively find element(s) mapped to workflow variable
//                    FormRow row = rows.iterator().next();
//                    Map<String, String> variableMap = new HashMap<String, String>();
//                    variableMap = storeWorkflowVariables(element, row, variableMap);
//
//                    if (activityId != null) {
//                        workflowManager.activityVariables(activityId, variableMap);
//                    } else {
//                        workflowManager.processVariables(processId, variableMap);
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//    public String getFormId() {
//        Form form = FormUtil.findRootForm(getElement());
//        return form.getPropertyString(FormUtil.PROPERTY_ID);
//    }
//
//    public String getTableName() {
//        Form form = FormUtil.findRootForm(getElement());
//        return form.getPropertyString(FormUtil.PROPERTY_TABLE_NAME);
//    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public DataListColumn[] getColumns() {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        
        Map binderProps = getProperties();
        Object datalistBinder = binderProps.get("datalistBinder");
        
        if (datalistBinder != null && datalistBinder instanceof Map) {
            Map fvMap = (Map) datalistBinder;
            if (fvMap != null && fvMap.containsKey("className") && !fvMap.get("className").toString().isEmpty()) {
                String className = fvMap.get("className").toString();
                DataListBinderDefault datalistBinderPlugin = (DataListBinderDefault)pluginManager.getPlugin(className);

                //obtain plugin defaults
                Map propertiesMap = new HashMap();
                propertiesMap.putAll(AppPluginUtil.getDefaultProperties((Plugin) datalistBinderPlugin, (Map) fvMap.get("properties"), null, null));

                if (datalistBinderPlugin instanceof PropertyEditable) {
                    ((PropertyEditable) datalistBinderPlugin).setProperties(propertiesMap);
                }

                return datalistBinderPlugin.getColumns();
            }
        }
        return null;
    }

    @Override
    public String getPrimaryKeyColumnName() {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        
        Map binderProps = getProperties();
        Object datalistBinder = binderProps.get("datalistBinder");
        
        if (datalistBinder != null && datalistBinder instanceof Map) {
            Map fvMap = (Map) datalistBinder;
            if (fvMap != null && fvMap.containsKey("className") && !fvMap.get("className").toString().isEmpty()) {
                String className = fvMap.get("className").toString();
                DataListBinderDefault datalistBinderPlugin = (DataListBinderDefault)pluginManager.getPlugin(className);

                //obtain plugin defaults
                Map propertiesMap = new HashMap();
                propertiesMap.putAll(AppPluginUtil.getDefaultProperties((Plugin) datalistBinderPlugin, (Map) fvMap.get("properties"), null, null));

                if (datalistBinderPlugin instanceof PropertyEditable) {
                    ((PropertyEditable) datalistBinderPlugin).setProperties(propertiesMap);
                }

                return datalistBinderPlugin.getPrimaryKeyColumnName();
            }
        }
        return null;
    }

    @Override
    public DataListCollection getData(DataList dl, Map map, DataListFilterQueryObject[] dlfqos, String string, Boolean bln, Integer intgr, Integer intgr1) {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        
        Map binderProps = getProperties();
        Object datalistBinder = binderProps.get("datalistBinder");
        
        if (datalistBinder != null && datalistBinder instanceof Map) {
            Map fvMap = (Map) datalistBinder;
            if (fvMap != null && fvMap.containsKey("className") && !fvMap.get("className").toString().isEmpty()) {
                String className = fvMap.get("className").toString();
                DataListBinderDefault datalistBinderPlugin = (DataListBinderDefault)pluginManager.getPlugin(className);

                //obtain plugin defaults
                Map propertiesMap = new HashMap();
                propertiesMap.putAll(AppPluginUtil.getDefaultProperties((Plugin) datalistBinderPlugin, (Map) fvMap.get("properties"), null, null));

                if (datalistBinderPlugin instanceof PropertyEditable) {
                    ((PropertyEditable) datalistBinderPlugin).setProperties(propertiesMap);
                }

                DataListCollection data = datalistBinderPlugin.getData(dl, map, dlfqos, string, bln, intgr, intgr1);
                
                Map scriptProperties = new HashMap();
                scriptProperties.put("data", data);
                scriptProperties.put("columns", getColumns());
                

                if ("true".equalsIgnoreCase(getPropertyString("debugMode"))) {
                    LogUtil.info(DatalistBinderWrapper.class.getName(), "Data from binder: " + data);
                }

                String script = (String) binderProps.get("script");

                //script = WorkflowUtil.processVariable(script, "", wfAssignment, "", replaceMap);
                DataListCollection formattedData = (DataListCollection) executeScript(script, scriptProperties);

                if ("true".equalsIgnoreCase(getPropertyString("debugMode"))) {
                    LogUtil.info(DatalistBinderWrapper.class.getName(), "Data after script: " + formattedData);
                }

                return formattedData;
            }
        }
        return null;
    }

    @Override
    public int getDataTotalRowCount(DataList dl, Map map, DataListFilterQueryObject[] dlfqos) {
        PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
        WorkflowManager workflowManager = (WorkflowManager) AppUtil.getApplicationContext().getBean("workflowManager");
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        
        Map binderProps = getProperties();
        Object datalistBinder = binderProps.get("datalistBinder");
        
        if (datalistBinder != null && datalistBinder instanceof Map) {
            Map fvMap = (Map) datalistBinder;
            if (fvMap != null && fvMap.containsKey("className") && !fvMap.get("className").toString().isEmpty()) {
                String className = fvMap.get("className").toString();
                DataListBinderDefault datalistBinderPlugin = (DataListBinderDefault)pluginManager.getPlugin(className);

                //obtain plugin defaults
                Map propertiesMap = new HashMap();
                propertiesMap.putAll(AppPluginUtil.getDefaultProperties((Plugin) datalistBinderPlugin, (Map) fvMap.get("properties"), null, null));

                if (datalistBinderPlugin instanceof PropertyEditable) {
                    ((PropertyEditable) datalistBinderPlugin).setProperties(propertiesMap);
                }

                return datalistBinderPlugin.getDataTotalRowCount(dl, map, dlfqos);
            }
        }
        return 0;
    }

}
