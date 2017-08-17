package org.cardiacatlas.xpacs;

import org.cardiacatlas.xpacs.config.ApplicationProperties;
import org.cardiacatlas.xpacs.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import org.dcm4che2.data.*;
import org.dcm4che2.io.*;
import org.dcm4che2.media.*;
import org.dcm4che2.net.*;
import org.dcm4che2.net.NetworkConnection;
import org.dcm4che2.tool.dcmecho.*;
import org.dcm4che2.tool.dcmqr.*;
import org.dcm4che2.tool.dcmsnd.DcmSnd;
import org.dcm4che2.util.*;

import javax.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.sql.*;


@ComponentScan
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class })
@EnableConfigurationProperties({ LiquibaseProperties.class, ApplicationProperties.class })
public class XpacswebApp {

	private static void testDatabase(String url) throws SQLException {
		Properties connectionProps = new Properties();
	    connectionProps.put("user", "xpacsweb");
	    Connection connection= DriverManager.getConnection(url, connectionProps);

	    Statement s=connection.createStatement();
	    try {
	    s.execute("DROP ALL");
	    } catch(SQLException sqle) {
	        System.out.println("Table not found, not dropping");
	    }
	    s.close();
	    connection.close();
	}

	private static final Logger log = LoggerFactory.getLogger(XpacswebApp.class);

	private final Environment env;

	public XpacswebApp(Environment env) {
		this.env = env;
	}

	/**
	 * Initializes xpacsweb.
	 * <p>
	 * Spring profiles can be configured with a program arguments
	 * --spring.profiles.active=your-active-profile
	 * <p>
	 * You can find more information on how profiles work with JHipster on
	 * <a href=
	 * "http://jhipster.github.io/profiles/">http://jhipster.github.io/profiles/</a>.
	 */
	@PostConstruct
	public void initApplication() {
		Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
			log.error("You have misconfigured your application! It should not run "
					+ "with both the 'dev' and 'prod' profiles at the same time.");
		}
		if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
				&& activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
			log.error("You have misconfigured your application! It should not"
					+ "run with both the 'dev' and 'cloud' profiles at the same time.");
		}
	}

	/**
	 * Main method, used to run the application.
	 *
	 * @param args
	 *            the command line arguments
	 * @throws UnknownHostException
	 *             if the local host name could not be resolved into an address
	 */
	public static void main(String[] args) throws UnknownHostException {

		SpringApplication app = new SpringApplication(XpacswebApp.class);
		DefaultProfileUtil.addDefaultProfile(app);
		Environment env = app.run(args).getEnvironment();
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}

		startDCM4CHEEServer(Boolean.parseBoolean(env.getProperty("application.pacsdb.init-start")));
		log.info(
				"\n----------------------------------------------------------\n\t"
						+ "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}\n\t"
						+ "External: \t{}://{}:{}\n\t"
						+ "Profile(s): \t{}\n----------------------------------------------------------",
				env.getProperty("spring.application.name"), protocol, env.getProperty("server.port"), protocol,
				InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"), env.getActiveProfiles());

		/*

		sendDicomFileTest();
		recieveDicomFileTest();



		try {
			testDatabase("jdbc:h2:mem:xpacsweb");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	private static void startDCM4CHEEServer(boolean rundcm4chee) {
		log.info(rundcm4chee ? "Starting dcm4chee server." : "Not starting dcm4chee server.");
		if (rundcm4chee) {
			Thread dcm4cheeServer = new Thread(null, null, "dcm4chee server") {
				public void run() {
					try {
						ProcessBuilder pb = new ProcessBuilder(
								"~/dbase/dcm4chee/dcm4chee-2.18.1-mysql/bin/run.sh");
						BufferedReader reader = new BufferedReader(new InputStreamReader(pb.start().getInputStream()));
						String line = null;
						while ((line = reader.readLine()) != null) {
							log.info(line);
						}

					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			};

			dcm4cheeServer.setDaemon(true);
			dcm4cheeServer.start();

			/*try{
				dcm4cheeServer.join();
			} catch (Exception e){
				log.error(e.getMessage());
			} finally {
				dcm4cheeServer.interrupt();
				log.info("DCM4CHEE Server interrupted.");
			}*/
		}
	}

	private static void sendDicomFileTest() {
		DcmSnd dcmsnd = new DcmSnd("DCM4CHEE");
		dcmsnd.setCalledAET("DCM4CHEE");
		dcmsnd.setRemoteHost("127.0.0.1");
		dcmsnd.setRemotePort(11112);
		dcmsnd.setStorageCommitment(true);
		UserIdentity userId = new UserIdentity.UsernamePasscode("admin", "admin".toCharArray());
		dcmsnd.setUserIdentity(userId);
		dcmsnd.addFile(new File(
				"/home/capdev/workspace/capdev/CAPServer2.0/xpacs/downloads/SampleData/CAP_RCH-00051-01_MR_2-chamber_FIESTA_CINE__hrt_raw_20160712151833885_2.dcm"));
		dcmsnd.configureTransferCapability();
		try {
			dcmsnd.start();
			dcmsnd.open();
			log.info("Connected to remote");
			dcmsnd.send();
			dcmsnd.commit();
			dcmsnd.close();
			log.info("Released connection to remote");
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			dcmsnd.stop();
		}
	}

	private static void recieveDicomFileTest() {
		DcmQR dcmqr = new DcmQR("DCM4CHEE");
		dcmqr.setCalledAET("DCM4CHEE", false);
		dcmqr.setRemoteHost("127.0.0.1");
		dcmqr.setRemotePort(11112);
		UserIdentity userId = new UserIdentity.UsernamePasscode("admin", "admin".toCharArray());
		dcmqr.setUserIdentity(userId);
		dcmqr.setQueryLevel(DcmQR.QueryRetrieveLevel.PATIENT);
		dcmqr.addMatchingKey(Tag.toTagPath("PatientID"), "*");
		dcmqr.setCGet(true);
		dcmqr.configureTransferCapability(true);

		try {
			dcmqr.start();
			log.info("started");
			dcmqr.open();
			log.info("opened");
			List<DicomObject> result = dcmqr.query();
			log.info("move");
			// dcmqr.move(result);
			dcmqr.get(result);
			dcmqr.close();
			for (DicomObject d : result) {
				log.info("DICOM OBJECT" + d.toString());

			}
			log.info("Finish result found " + result.size() + " dicom objects.");
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			dcmqr.stop();
		}
	}
}
