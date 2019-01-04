package com.binomisoft.redviera.dicom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity(name="studies")
public class Study {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "studyinsta")
	private String studyinsta;
	
	@Column(name = "patientname")
	private String patientname;
	
	@Column(name = "birthdate")
	private String birthdate;
	
	@Column(name = "age")
	private String age;
	
	@Column(name = "genre")
	private String genre;
	
	@Column(name = "modality")
	private String modality;
	
	@Column(name = "submodality")
	private String submodality;
	
	@Column(name = "studydate")
	private String studydate;
	
	@Column(name = "company")
	private String company;
	
	@Column(name = "series")
	private long series;
	
	@Column(name = "images")
	private long images;
	
	@Column(name = "petition")
	private String petition;
	
	@Column(name = "priority")
	private String priority;
	
	@Column(name = "weight")
	private long weight;
	
	@Column(name = "studystatus")
	private String studystatus;
	
	@ManyToOne
	private Doctor doctor;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public String getPatientname() {
		return patientname;
	}

	public void setPatientname(String patientname) {
		this.patientname = patientname;
	}

	public String getStudydate() {
		return studydate;
	}

	public void setStudydate(String studydate) {
		this.studydate = studydate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public String getSubmodality() {
		return submodality;
	}

	public void setSubmodality(String submodality) {
		this.submodality = submodality;
	}

	public String getStudyDate() {
		return studydate;
	}

	public void setStudyDate(String studydate) {
		this.studydate = studydate;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public long getSeries() {
		return series;
	}

	public void setSeries(long series) {
		this.series = series;
	}

	public long getImages() {
		return images;
	}

	public void setImages(long images) {
		this.images = images;
	}

	public String getPetition() {
		return petition;
	}

	public void setPetition(String petition) {
		this.petition = petition;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStudystatus() {
		return studystatus;
	}

	public void setStudystatus(String studystatus) {
		this.studystatus = studystatus;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public long getWeight() {
		return weight;
	}

	public void setWeight(long weight) {
		this.weight = weight;
	}

	public String getStudyinsta() {
		return studyinsta;
	}

	public void setStudyinsta(String studyinsta) {
		this.studyinsta = studyinsta;
	}	
}
