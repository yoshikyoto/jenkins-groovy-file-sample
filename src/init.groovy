import jenkins.model.*

/*
 * groovy for initialize jenkins
 * /var/lib/jenkins/init.groovy
 */

/** jenkins instance */
def instance  = Jenkins.getInstance()

/** required plugins */
def plugins   = ["git", "ssh-slaves", "workflow-aggregator", "github-pullrequest"]

pluginManager = instance.getPluginManager()
updateCenter = instance.getUpdateCenter()

updateCenter.updateAllSites()

/** if not enabled <pluginName>, install and enable it */
def enablePlugin(pluginName) {
  // if not installed, install the plugin
  if (! pluginManager.getPlugin(pluginName)) {
    deployment = updateCenter.getPlugin(pluginName).deploy(true)
    deployment.get()
  }

  // if not enabled, enable the plugin
  def plugin = pluginManager.getPlugin(pluginName)
  if (! plugin.isEnabled()) {
    plugin.enable()
  }

  plugin.getDependencies().each {
    enablePlugin(it.shortName)
  }
}

plugins.each {
  def plugin = pluginManager.getPlugin(it)
  enablePlugin(it)
}
