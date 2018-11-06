# Import the Portal object.
import geni.portal as portal
# Import the ProtoGENI library.
import geni.rspec.pg as pg
import geni.rspec.igext as IG

# Create a portal context.
pc = portal.Context()

# Create a Request object to start building the RSpec.
request = pc.makeRequestRSpec()


tourDescription = \
"""
This profile provides the template for a Hadoop cluster with 1 name node and 3 data node.
"""

#
# Setup the Tour info with the above description and instructions.
#  
tour = IG.Tour()
tour.Description(IG.Tour.TEXT,tourDescription)
request.addTour(tour)

prefixForIP = "192.168.1."

link = request.LAN("lan")

for i in range(4):
  if i == 0:
    node = request.RawPC("namenode")
  else:
<<<<<<< HEAD
    node = request.XenVM("compute-" + str(i-2))
    node.cores = 4
    node.ram = 4096
    
  node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:CENTOS7-64-STD"
  
  iface = node.addInterface("if" + str(i))
=======
    node = request.RawPC("datanode-" + str(i))

  node.routable_control_ip = "true"
  node.disk_image = "urn:publicid:IDN+emulab.net+image+emulab-ops:CENTOS7-64-STD"
  bs = node.Blockstore("bs" + str(i), "/hadoop")
  bs.size = "50GB"
    
  iface = node.addInterface("if" + str(i-3))
>>>>>>> 364d8a06c6ef958c64046af82c89503df5596725
  iface.component_id = "eth1"
  iface.addAddress(pg.IPv4Address(prefixForIP + str(i + 1), "255.255.255.0"))
  link.addInterface(iface)
  
  node.addService(pg.Execute(shell="sh", command="sudo chmod 755 /local/repository/passwordless.sh"))
  node.addService(pg.Execute(shell="sh", command="sudo /local/repository/passwordless.sh"))
  node.addService(pg.Execute(shell="sh", command="sudo chmod 755 /local/repository/environment_prep.sh"))
  node.addService(pg.Execute(shell="sh", command="sudo /local/repository/environment_prep.sh"))
  
<<<<<<< HEAD
  # This code segment is added per Benjamin Walker's solution to address the StrictHostKeyCheck issue of ssh
  node.addService(pg.Execute(shell="sh", command="sudo chmod 755 /local/repository/ssh_setup.sh"))
  node.addService(pg.Execute(shell="sh", command="sudo -H -u lngo bash -c '/local/repository/ssh_setup.sh'"))
 
  node.addService(pg.Execute(shell="sh", command="sudo su lngo -c 'cp /local/repository/source/* /users/lngo'"))
=======
  if i == 0:
    node.addService(pg.Execute(shell="sh", command="sudo yum install -y ambari-server"))
>>>>>>> 364d8a06c6ef958c64046af82c89503df5596725
  
  node.addService(pg.Execute(shell="sh", command="sudo yum install -y ambari-agent"))
  node.addService(pg.Execute(shell="sh", command="sudo sed -i 's/localhost/192.168.1.1/g' /etc/ambari-agent/conf/ambari-agent.ini"))
  node.addService(pg.Execute(shell="sh", command="sudo ambari-agent start"))
    
# Print the RSpec to the enclosing page.
pc.printRequestRSpec(request)
