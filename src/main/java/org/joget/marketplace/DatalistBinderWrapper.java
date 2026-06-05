package org.joget.marketplace;

import java.util.HashMap;
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
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.Plugin;
import org.joget.plugin.base.PluginManager;
import org.joget.plugin.property.model.PropertyEditable;
import org.joget.workflow.model.service.WorkflowManager;

public class DatalistBinderWrapper extends DataListBinderDefault{
    private static String MESSAGE_PATH = "messages/datalistBinderWrapper";
    
    @Override
    public String getName() {
        return "Datalist Binder Wrapper";
    }

    @Override
    public String getVersion() {
        return "8.0.1";
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
            LogUtil.debug(getClass().getName(), "Executing script " + script);
            result = AppPluginUtil.executeScript(script, properties);
            return result;
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error executing script");
            return null;
        }
    }

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

                DataListCollection data = datalistBinderPlugin.getData(dl, propertiesMap, dlfqos, string, bln, intgr, intgr1);
                
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

                return datalistBinderPlugin.getDataTotalRowCount(dl, propertiesMap, dlfqos);
            }
        }
        return 0;
    }

}
