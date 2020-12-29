package com.atlan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.atlan.entity.CsvStructure;
import com.atlan.entity.Job;
import com.atlan.entity.dao.Csvrepo;
import com.atlan.entity.dao.JobRepo;


@RestController
public class AppController {
	
	@Autowired
	JobRepo jobrepo;
	@Autowired
	Csvrepo csvrepo;
	public static ReentrantLock lock = new ReentrantLock();
	
@GetMapping(value="/aliens")
Alien hello()
{
	return new Alien();
}


//it is a starting point which read the files after that save on server and then start saving file data in database
@PostMapping("/start")
public Job fileUpload(@RequestParam("file") MultipartFile file) {
    if(lock.tryLock()) {
    	
	if (file.isEmpty()) {
		lock.unlock();
		return new Job(0,"file is empty please use valid file",null);
	}
	else if(!(file.getContentType().equals("text/csv"))) {
		lock.unlock();
		return new Job(0,"file should be of type csv forexample(fileName.csv)",null);
	}
	try {
		// read and write the file to the slelected location-
		byte[] bytes = file.getBytes();
		Path path = Paths.get( file.getOriginalFilename());
		Files.write(path, bytes);
	} catch (IOException e) {
		lock.unlock();
		return new Job(0,"csv file Not Found",null);
		
	}
    
    Job jobstatus=new Job();
    jobstatus.setFilename(file.getOriginalFilename());
    jobstatus.setStatus("processing");
    jobrepo.save(jobstatus);
    jobstatus.setFilename(jobstatus.getId()+"-"+jobstatus.getFilename());
    jobrepo.save(jobstatus);
    JobThread job=new JobThread(jobstatus.getFilename());
    job.setName(String.valueOf(jobstatus.getId()));
     job.setExplicitId(String.valueOf(jobstatus.getId()));
     File oldfile=new File(file.getOriginalFilename());
     File newfile=new File(jobstatus.getFilename());
     oldfile.renameTo(newfile);
    job.start();
    return jobstatus;
    }
    else
    {
    	return new Job(0,"One Job Already Running","Wait or check status by {base_url}/jobs endpoint");
    }
    	

	

}

//for get all jobs status
@GetMapping("/jobs")
public List<Job> getJobs()
{
	Iterable<Job> iterable=jobrepo.findAll();
	List<Job> list = StreamSupport 
            .stream(iterable.spliterator(), false) 
            .collect(Collectors.toList());
	return list;
}

//This point tells about the status of the job with specified job id
@GetMapping("/status/{jobid}")
public Job status(@PathVariable long jobid)
{     
	     if(jobrepo.existsById(jobid))
		return jobrepo.findById(jobid).get();
	     return new Job(jobid,"No job Exists with this Id","Null");
}



//this point returns jason form data with respect to given job
@GetMapping("/data/{jobid}")
public List<CsvStructure> getData(@PathVariable long jobid)
{
	if(lock.tryLock())
	{
	Iterable<CsvStructure> iterable=csvrepo.findAllByJobid(String.valueOf(jobid));
	List<CsvStructure> list = StreamSupport 
            .stream(iterable.spliterator(), false) 
            .collect(Collectors.toList());
	try{lock.unlock();}finally {lock.unlock();}
	return list;}
return null;
}


//this endpoint deletes all the data processed from file to database by given jobid
@GetMapping("/delete/{jobid}")
public Job deleteJob(@PathVariable long jobid)
{
	if(lock.tryLock())
	{
	Job job=jobrepo.findById(jobid).get();
	job.setStatus("deleted");
	job.setRecords(0);
	jobrepo.save(job);
	csvrepo.deleteCsvStructureByJobid(String.valueOf(jobid));
	lock.unlock();
	return job;
	}
	else
	{
		return new Job(0,"One Job Already Running","Wait or check status by {base_url}/jobs endpoint");
	}
}


//this endpoint terminate and rollback the running job with particular jobid
@GetMapping("/terminate/{jobid}")
public Job stopJob(@PathVariable long jobid)
{
	
	Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
	Job job=new Job(0101,"No Job Active With this Id",null);
	boolean status=true;
	for(Thread thread : setOfThread){
	    if(thread.getName().equals(String.valueOf(jobid))){
	        JobThread jb=(JobThread)thread;
	        job=jobrepo.findById(jobid).get();
			job.setStatus("terminated");
			jobrepo.save(job);
	        jb.active=false;
	        csvrepo.deleteCsvStructureByJobid(String.valueOf(jobid));
	        job=jobrepo.findById(jobid).get();
	        job.setRecords(0);
	        jobrepo.save(job);
	        status=false;
	    }
	}
	
	if(status&&jobrepo.findById(jobid).get().getStatus().equals("paused"))
	{
		if(lock.tryLock())
		{
		job=jobrepo.findById(jobid).get();
		job.setStatus("terminated");
		job.setRecords(0);
		jobrepo.save(job);
		csvrepo.deleteCsvStructureByJobid(String.valueOf(jobid));
		}
		else
			job=new Job(0,"One Job Already Running","Wait or check status by {base_url}/jobs endpoint");
	}
		return job;
}


//this endpoint resume the paused job 
@GetMapping("/start/{jobid}")
public Job resumeJob(@PathVariable long jobid)
{
	
	Job jobstatus=new Job(0,"No Job paused with this Id",null);
	if(jobrepo.existsById(jobid))
	{
	 jobstatus=jobrepo.findById(jobid).get();
	 if(jobstatus.getStatus().equals("paused"))
	 {
		 if(lock.tryLock())
		 {
			 
	JobThread job=new JobThread(jobstatus.getFilename());
	job.records=jobstatus.getRecords();
	job.setName(String.valueOf(jobstatus.getId()));
	jobstatus.setStatus("processing");
    jobrepo.save(jobstatus);
    job.setExplicitId(String.valueOf(jobid));
    job.start();
		 }
		 else
		 {
			 jobstatus.setStatus("One Job Already Running");
		 }
	 }
	 else
	 {
		 jobstatus=jobrepo.findById(jobid).get();
		 jobstatus.setStatus("Already "+jobstatus.getStatus());
	 }
	}
    return jobstatus;

		
}


//This endpoint pause the job with given jobid as path variable
@GetMapping("/stop/{jobid}")
public Job pauseJob(@PathVariable long jobid) throws InterruptedException
{
	
	Set<Thread> setOfThread = Thread.getAllStackTraces().keySet();
	Job job=null;
	
	for(Thread thread : setOfThread){
	    if(thread.getName().equals(String.valueOf(jobid))){
	    	job=jobrepo.findById(jobid).get();
	        JobThread jb=(JobThread)thread;
	        job.setStatus("paused");
	        jobrepo.save(job);
	        jb.active=false;
	        job.setRecords(jb.records);
	        jobrepo.save(job);
	       
	    }
	}
	if(job==null)
		job=new Job(0,"No Job Active With this Id",null);
	return job;
}

//Job Thread
class JobThread extends Thread
{
   public boolean active=true;
   private String fileName,jobid;
   long records=0;
	public JobThread(String fileName) {
	this.fileName=fileName;
}
	public void setExplicitId(String jobid) {
		this.jobid=jobid;
	}
	
	
	//The job of retriving data from file and store in database is done by this thread job
	@Override
	public void run() {
		long temprecords=0;
		try {
			BufferedReader br= new BufferedReader(new FileReader(fileName)); 
			String line = "";  
			String splitBy = ","; 
			while(records>temprecords&&(line = br.readLine()) != null&&active)
				temprecords++;
			while ((line = br.readLine()) != null&&active)   //returns a Boolean value  
			{  records++;
			String[] employee = line.split(splitBy);    // use comma as separator
			if(employee.length==6)
			{
			CsvStructure csvstructure=new CsvStructure(jobid,employee[0],employee[1],employee[2],employee[3],employee[4],employee[5]);
			csvrepo.save(csvstructure);
			}
			}
			try {Thread.sleep(3000);}catch(Exception e) {}
			Job jb=jobrepo.findById(Long.parseLong(jobid)).get();
			    if(jb.getStatus().equals("processing"))
			    {
			     jb.setStatus("Job completed");
			     jb.setRecords(records);
			    }
			jobrepo.save(jb);
			br.close();
			
			}   
			
			catch (IOException e)   
			{  
				
			e.printStackTrace();  
			}
		finally
		{
			System.out.println("Unlock");
			lock = new ReentrantLock();
		}
		
	}	
}
//RestController end here
}