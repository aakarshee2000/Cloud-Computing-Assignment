/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */


package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * An example showing how to create
 * scalable simulations.
 */
public class CloudSimExample6 {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList1;
	private static List<Cloudlet> cloudletList2;
	private static List<Cloudlet> cloudletList3;
	
	/** The vmlist. */
	private static List<Vm> vmlist1;
	private static List<Vm> vmlist2;
	private static List<Vm> vmlist3;

	private static List<Vm> createVM(int userId, int vms) {

		//Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();

		//VM Parameters
		long size = 10000; //image size (MB)
		int ram = 512; //vm memory (MB)
		int mips = 1000;
		long bw = 1000;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

		//create VMs
		Vm[] vm = new Vm[vms];

		for(int i=0;i<vms;i++){
			vm[i] = new Vm(i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
			//for creating a VM with a space shared scheduling policy for cloudlets:
			//vm[i] = Vm(i, userId, mips, pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerSpaceShared());

			list.add(vm[i]);
		}

		return list;
	}


	private static List<Cloudlet> createCloudlet(int userId, int cloudlets){
		// Creates a container to store Cloudlets
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

		//cloudlet parameters
		long length = 1000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudlets];

		for(int i=0;i<cloudlets;i++){
			cloudlet[i] = new Cloudlet(i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			// setting the owner of these Cloudlets
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}

		return list;
	}


	////////////////////////// STATIC METHODS ///////////////////////

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		Log.printLine("Starting CloudSimExample6...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 3;   // number of grid users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the CloudSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			@SuppressWarnings("unused")
			Datacenter datacenter1 = createDatacenter("Datacenter_1");
			@SuppressWarnings("unused")
			Datacenter datacenter2 = createDatacenter("Datacenter_2");
			
			//Third step: Create Broker
			//DatacenterBroker broker = createBroker();
			//int brokerId = broker.getId();
			
			DatacenterBroker broker3 = createBroker(3);
			int brokerId3 = broker3.getId();
			
			DatacenterBroker broker2 = createBroker(2);
			int brokerId2 = broker2.getId();
			
			//Third step: Create Brokers
			DatacenterBroker broker1 = createBroker(1);
			int brokerId1 = broker1.getId();
			
			//Log.printLine(brokerId2);
			//Fourth step: Create VMs and Cloudlets and send them to broker
			
			vmlist3 = createVM(brokerId3,4); //creating 10 vms
			cloudletList3 = createCloudlet(brokerId3,6); // creating 20 cloudlets
			
			vmlist2 = createVM(brokerId2,3); //creating 10 vms
			cloudletList2 = createCloudlet(brokerId2,5); // creating 20 cloudlets
			
			//Fourth step: Create VMs and Cloudlets and send them to broker
			vmlist1 = createVM(brokerId1,6); //creating 20 vms
			cloudletList1 = createCloudlet(brokerId1,8); // creating 40 cloudlets
			
			broker3.submitVmList(vmlist3);
			broker2.submitVmList(vmlist2);
			broker1.submitVmList(vmlist1);
			
			broker3.submitCloudletList(cloudletList3);
			broker2.submitCloudletList(cloudletList2);
			broker1.submitCloudletList(cloudletList1);
			
			// Fifth step: Starts the simulation
			CloudSim.startSimulation();
			
			List<Cloudlet> newList3 = broker3.getCloudletReceivedList();
			List<Cloudlet> newList2 = broker2.getCloudletReceivedList();
			List<Cloudlet> newList1 = broker1.getCloudletReceivedList();
			
			int[] allocations = new int[3];
			int[] capacity = new int[3];
			
			capacity[0] = 10;
			capacity[1] = 10;
			capacity[2] = 10;
			
			int l3 = newList3.size();
			int l2 = newList2.size();
			int l1 = newList1.size();
			
			//Log.printLine(newList3.get(0).getResourceId());
			
			Arrays.fill(allocations, 0);
			
			List<Cloudlet> activelist = newList3;
			int residue = 0;
			
			for(int j = 0; j != l3 + l2 + l1; ++j)
			{
				if(j < l3)
				{
					activelist = newList3;
					residue = 0;
				}
				else if(j < l3 + l2)
				{
					activelist = newList2;
					residue = l3;
				}
				else
				{
					activelist = newList1;
					residue = l3 + l2;
				}
				
				allocations[activelist.get(j - residue).getResourceId() - 2]++;
			}
			
				for(int j = 0; j != l3 + l2 + l1; j++)
				{
					//Calculate cost
					int cost = calculate_cost(allocations, capacity);
					
					if(j < l3)
					{
						activelist = newList3;
						residue = 0;
					}
					else if(j < l3 + l2)
					{
						activelist = newList2;
						residue = l3;
					}
					else
					{
						activelist = newList1;
						residue = l3 + l2;
					}
					

					Cloudlet c = activelist.get(j - residue);
					
					allocations[c.getResourceId() - 2]--;
					
					allocations[(c.getResourceId() + 2)%3]++;
					
					c.setResourceParameter((c.getResourceId() + 2)%3 + 2, 0.1);
					
					int newcost = calculate_cost(allocations, capacity);
					
					if(newcost > cost) {
						
						c.setResourceParameter(c.getResourceId()%3 + 2, 0.1);
						
						allocations[c.getResourceId() - 2]++;
						
						allocations[(c.getResourceId() + 2)%3]--;
						
					}
					
				}
			
			CloudSim.stopSimulation();
			
			// Final step: Print results when simulation is over
			Log.print("=============> User "+(brokerId3 - 4)+"    ");
			printCloudletList(newList3);
			
			Log.print("=============> User "+(brokerId2 - 4)+"    ");
			printCloudletList(newList2);
			
			Log.print("=============> User "+(brokerId1 - 4)+"    ");
			printCloudletList(newList1);
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	public static int calculate_cost(int[] allocations, int[] capacity) {
		
		//Calculate cost
		int cost = (capacity[0] - allocations[0]) * (capacity[0] - allocations[0]) +   
		(capacity[1] - allocations[1]) * (capacity[1] - allocations[1]) +
		(capacity[2] - allocations[2]) * (capacity[2] - allocations[2]);
		
		return cost;
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store one or more
		//    Machines
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
		//    create a list to store these PEs before creating
		//    a Machine.
		List<Pe> peList1 = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into the list.
		//for a quad-core machine, a list of 4 PEs is required:
		peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
		peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

		//Another list, for a dual-core machine
		List<Pe> peList2 = new ArrayList<Pe>();

		peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
		peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

		//4. Create Hosts with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList1,
    				new VmSchedulerTimeShared(peList1)
    			)
    		); // This is our first machine

		hostId++;

		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList2,
    				new VmSchedulerTimeShared(peList2)
    			)
    		); // Second machine


		//To create a host with a space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerSpaceShared(peList1)
    	//		)
    	//	);

		//To create a host with a oportunistic space-shared allocation policy for PEs to VMs:
		//hostList.add(
    	//		new Host(
    	//			hostId,
    	//			new CpuProvisionerSimple(peList1),
    	//			new RamProvisionerSimple(ram),
    	//			new BwProvisionerSimple(bw),
    	//			storage,
    	//			new VmSchedulerOportunisticSpaceShared(peList1)
    	//		)
    	//	);


		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.1;	// the cost of using storage in this resource
		double costPerBw = 0.1;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(int id){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker"+id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + indent + dft.format(cloudlet.getActualCPUTime()) +
						indent + indent + dft.format(cloudlet.getExecStartTime())+ indent + indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
}
