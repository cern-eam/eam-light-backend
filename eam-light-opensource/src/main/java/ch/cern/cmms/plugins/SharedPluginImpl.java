package ch.cern.cmms.plugins;

public class SharedPluginImpl implements SharedPlugin {
    @Override
    public String sayHello() {
        return "Hello from Open Source!";
    }
}
